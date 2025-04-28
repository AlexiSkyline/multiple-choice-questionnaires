package org.skyline.mcq.infrastructure.inputadapter;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.CategoryInputPort;
import org.skyline.mcq.infrastructure.utils.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryAPI {

    public static final String CATEGORY_PATH = "/api/v1/categories";
    public static final String CATEGORY_PATH_ID = CATEGORY_PATH + "/{categoryId}";

    private final CategoryInputPort categoryInputPort;
    private final ResponseHandler responseHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(CATEGORY_PATH)
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SURVEY_CREATOR')")
    public ResponseEntity<ResponseBody<CategoryResponseDto>> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        UUID accountId = jwtTokenProvider.getCurrentUserDetails().getId();
        return categoryInputPort.saveCategory(accountId, categoryRequestDto).map(category -> responseHandler.responseBuild(
                HttpStatus.CREATED,
                "Category Created Successfully",
                category
        )).orElseThrow(() -> new NotFoundException("Account", accountId.toString(), "Please provide a valid account ID"));
    }

    @GetMapping(CATEGORY_PATH)
    public ResponseEntity<ResponseBody<Page<CategoryResponseDto>>> getAllCategories(@RequestParam(required = false) String title,
                                                                                    @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                    @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Requested All Category are given here",
                categoryInputPort.listCategories(null, title, true, pageNumber, pageSize)
        );
    }

    @GetMapping(CATEGORY_PATH + "/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<ResponseBody<Page<CategoryResponseDto>>> getAllCategories(@RequestParam(required = false) UUID accountId,
                                                                                    @RequestParam(required = false) String title,
                                                                                    @RequestParam(required = false) Boolean isActive,
                                                                                    @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                    @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "ADMIN: Requested All Category are given here",
                categoryInputPort.listCategories(accountId, title, isActive, pageNumber, pageSize)
        );
    }

    @GetMapping(CATEGORY_PATH + "/creator")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('SURVEY_CREATOR')")
    public ResponseEntity<ResponseBody<Page<CategoryResponseDto>>> getAllCategories(@RequestParam(required = false) UUID accountId,
                                                                                    @RequestParam(required = false) String title,
                                                                                    @RequestParam(required = false) @Positive Integer pageNumber,
                                                                                    @RequestParam(required = false) @Positive Integer pageSize) {
        return responseHandler.responseBuild(
                HttpStatus.OK,
                "Creator: Requested All Category are given here",
                categoryInputPort.listCategories(accountId, title, true, pageNumber, pageSize)
        );
    }

    @PutMapping(CATEGORY_PATH_ID)
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SURVEY_CREATOR')")
    public ResponseEntity<Void> updateCategory(@PathVariable("categoryId") UUID categoryId,
                                               @Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        categoryInputPort.updateCategory(categoryId, jwtTokenProvider.getCurrentUserDetails().getId(), categoryRequestDto).orElseThrow(() -> new NotFoundException(
                "Category",
                categoryId.toString(),
                "Please provide a valid category ID"
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(CATEGORY_PATH_ID)
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'SURVEY_CREATOR')")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") UUID categoryId) {

        if (Boolean.FALSE.equals(categoryInputPort.deleteCategoryByIdAndByAccountId(categoryId, jwtTokenProvider.getCurrentUserDetails().getId()))) {
            throw new NotFoundException(
                    "Category",
                    categoryId.toString(),
                    "Please provide a valid category ID"
            );
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
