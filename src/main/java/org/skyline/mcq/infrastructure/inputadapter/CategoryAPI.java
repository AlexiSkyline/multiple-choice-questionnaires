package org.skyline.mcq.infrastructure.inputadapter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.skyline.mcq.application.dtos.input.CategoryRequestDto;
import org.skyline.mcq.application.dtos.output.CategoryResponseDto;
import org.skyline.mcq.domain.exceptions.NotFoundException;
import org.skyline.mcq.infrastructure.http.ResponseHandler;
import org.skyline.mcq.infrastructure.http.dto.ResponseBody;
import org.skyline.mcq.infrastructure.inputport.CategoryInputPort;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryAPI {

    public static final String CATEGORY_PATH = "/api/v1/categories";
    public static final String CATEGORY_PATH_ID = CATEGORY_PATH + "/{categoryId}";

    private final CategoryInputPort categoryInputPort;
    private final ResponseHandler responseHandler;

    @PostMapping(CATEGORY_PATH)
    public ResponseEntity<ResponseBody<CategoryResponseDto>> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        return categoryInputPort.saveCategory(categoryRequestDto).map(category -> responseHandler.responseBuild(
                HttpStatus.CREATED,
                "Category Created Successfully",
                category
        )).orElseThrow(() -> new NotFoundException(
                "Account",
                categoryRequestDto.getAccountId().toString(),
                "Please provide a valid account ID"
        ));
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

    @PutMapping(CATEGORY_PATH_ID)
    public ResponseEntity<Void> updateCategory(@PathVariable("categoryId") UUID categoryId,
                                               @Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        categoryInputPort.updateCategory(categoryId, categoryRequestDto).orElseThrow(() -> new NotFoundException(
                "Category",
                categoryId.toString(),
                "Please provide a valid category ID"
        ));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(CATEGORY_PATH_ID)
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") UUID categoryId) {

        if (Boolean.FALSE.equals(categoryInputPort.deleteCategory(categoryId))) {
            throw new NotFoundException(
                    "Category",
                    categoryId.toString(),
                    "Please provide a valid category ID"
            );
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
