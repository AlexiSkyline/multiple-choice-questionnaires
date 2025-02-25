package org.skyline.mcq.infrastructure.bootstrap;

import lombok.RequiredArgsConstructor;
import org.skyline.mcq.domain.enums.TypeRole;
import org.skyline.mcq.domain.models.Account;
import org.skyline.mcq.domain.models.Category;
import org.skyline.mcq.domain.models.Role;
import org.skyline.mcq.infrastructure.outputport.CategoryRepository;
import org.skyline.mcq.infrastructure.outputport.RoleRepository;
import org.skyline.mcq.infrastructure.outputport.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        loadRoleData();
        loadUserData();
        loadCategoryData();
    }

    public void loadRoleData() {

        if (roleRepository.count() == 0) {
            var admin = Role.builder()
                    .name(TypeRole.ADMIN)
                    .description("Administrator")
                    .build();

            var surveyCreator = Role.builder()
                    .name(TypeRole.SURVEY_CREATOR)
                    .description("Survey Creator")
                    .build();

            var surveyRespondent = Role.builder()
                    .name(TypeRole.SURVEY_RESPONDENT)
                    .description("Survey Respondent")
                    .build();

            roleRepository.saveAll(Arrays.asList(admin, surveyCreator, surveyRespondent));
        }
    }

    public void loadUserData() {
        if (accountRepository.count() == 0) {
            var admin = Account.builder()
                    .firstName("Diana")
                    .lastName("Taylor")
                    .username("diana_admin")
                    .email("diana.taylor@example.com")
                    .password("DianaPassword123")
                    .profileImage("admin1.jpg")
                    .description("Diana Administrator")
                    .build();

            var surveyCreator = Account.builder()
                    .firstName("Ethan")
                    .lastName("Miller")
                    .username("ethan_creator")
                    .email("ethan.miller@example.com")
                    .password("EthanPassword123")
                    .profileImage("creator2.jpg")
                    .description("Poll Maker")
                    .build();

            var surveyRespondent = Account.builder()
                    .firstName("George")
                    .lastName("Martinez")
                    .username("george_respondent")
                    .email("george.martinez@example.com")
                    .password("GeorgePassword123")
                    .profileImage("respondent1.jpg")
                    .description("George Participant")
                    .build();

            accountRepository.saveAll(Arrays.asList(admin, surveyCreator, surveyRespondent));
        }
    }

    public void loadCategoryData() {
        var admin = accountRepository.findAll().getFirst();

        if (categoryRepository.count() == 0 && admin != null) {

            var academic = Category.builder()
                    .title("Academic")
                    .description("Mathematics, Science, History, Idioms .etc")
                    .image("https://static.vecteezy.com/system/resources/previews/009/342/513/non_2x/graduation-clipart-design-illustration-free-png.png")
                    .account(admin)
                    .build();

            var psychometric = Category.builder()
                    .title("Psychometric")
                    .description("Cognitive evaluations, intelligence, logical reasoning")
                    .image("https://miro.medium.com/v2/resize:fit:736/0*eC_zKycKwAD8ymkD")
                    .account(admin)
                    .build();

            var professional = Category.builder()
                    .title("professional")
                    .description("Technical certifications (AWS, PMP), labor tests.")
                    .image("https://blog.testvocacional.app/uploads/images/202112/image_750x_61cb2ed11b60b.jpg")
                    .account(admin)
                    .build();

            var language = Category.builder()
                    .title("Language")
                    .description("English, Spanish, TOEFL, IELTS, DELE, JLPT.")
                    .image("https://www.edulyte.com/wp-content/uploads/2022/08/English-Test-1-1024x683.png")
                    .account(admin)
                    .build();

            categoryRepository.saveAll(Arrays.asList(academic, psychometric, professional, language));
        }
    }
}
