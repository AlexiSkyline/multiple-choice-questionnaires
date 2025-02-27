INSERT INTO account (id, first_name, last_name, username, email, password, active, created_at, updated_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'John', 'Doe', 'johndoe', 'john@example.com', 'password', 1, NOW(), NOW());

INSERT INTO category (id, title, image, description, account_id, active, created_at, updated_at)
VALUES
    ('22222222-2222-2222-2222-222222222222', 'Category 1', 'http://example.com/cat1.png', 'Descripción de la Categoría 1', '11111111-1111-1111-1111-111111111111', 1, NOW(), NOW()),
    ('33333333-3333-3333-3333-333333333333', 'Category 2', 'http://example.com/cat2.png', 'Descripción de la Categoría 2', '11111111-1111-1111-1111-111111111111', 1, NOW(), NOW());

INSERT INTO survey (id, title, image, description, max_points, question_count, category_id, active, time_limit, account_id, attempts, is_public, status, password, created_at, updated_at)
VALUES
    ('44444444-4444-4444-4444-444444444444', 'Survey 1 - Cat 1', 'http://example.com/survey1.png', 'Encuesta para Categoría 1', 100, 10, '22222222-2222-2222-2222-222222222222', 1, 3600, '11111111-1111-1111-1111-111111111111', 1, 0, 1, NULL, NOW(), NOW()),
    ('55555555-5555-5555-5555-555555555555', 'Survey 2 - Cat 1', 'http://example.com/survey2.png', 'Otra encuesta para Categoría 1', 100, 15, '22222222-2222-2222-2222-222222222222', 0, 3600, '11111111-1111-1111-1111-111111111111', 1, 1, 0, NULL, NOW(), NOW());

INSERT INTO survey (id, title, image, description, max_points, question_count, category_id, active, time_limit, account_id, attempts, is_public, status, password, created_at, updated_at)
VALUES
    ('66666666-6666-6666-6666-666666666666', 'Survey 1 - Cat 2', 'http://example.com/survey3.png', 'Encuesta para Categoría 2', 100, 10, '33333333-3333-3333-3333-333333333333', 0, 3600, '11111111-1111-1111-1111-111111111111', 1, 1, 0, NULL, NOW(), NOW()),
    ('77777777-7777-7777-7777-777777777777', 'Survey 2 - Cat 2', 'http://example.com/survey4.png', 'Otra encuesta para Categoría 2', 100, 20, '33333333-3333-3333-3333-333333333333', 0, 3600, '11111111-1111-1111-1111-111111111111', 1, 1, 1, NULL, NOW(), NOW()),
    ('88888888-8888-8888-8888-888888888888', 'Survey 3 - Cat 2', 'http://example.com/survey5.png', 'Otra encuesta para Categoría 2', 100, 20, '33333333-3333-3333-3333-333333333333', 1, 3600, '11111111-1111-1111-1111-111111111111', 1, 1, 0, NULL, NOW(), NOW());

INSERT INTO question (id, content, image, points, allowed_answers, options, correct_answers, survey_id, created_at, updated_at) VALUES
                                                                                                                                    ('55555555-5555-5555-5555-555555555555', 'What is the capital of France?', NULL, 10, 1, '["Paris", "London", "Berlin", "Madrid"]', '["Paris"]', '44444444-4444-4444-4444-444444444444', NOW(), NOW()),
                                                                                                                                    ('66666666-6666-6666-6666-666666666666', 'Which of the following are programming languages?', NULL, 15, 2, '["Python", "HTML", "Java", "CSS"]', '["Python", "Java"]', '44444444-4444-4444-4444-444444444444', NOW(), NOW()),
                                                                                                                                    ('77777777-7777-7777-7777-777777777777', 'Solve: 5 + 3 × 2', NULL, 10, 1, '["10", "11", "13", "16"]', '["11"]', '44444444-4444-4444-4444-444444444444', NOW(), NOW());
INSERT INTO question (id, content, image, points, allowed_answers, options, correct_answers, survey_id, created_at, updated_at) VALUES
                                                                                                                                    ('99999999-9999-9999-9999-999999999999', 'What does DNA stand for?', NULL, 10, 1, '["Deoxyribonucleic Acid", "Dynamic Neural Algorithm", "Data Network Array", "Digital Numerical Analysis"]', '["Deoxyribonucleic Acid"]', '88888888-8888-8888-8888-888888888888', NOW(), NOW()),
                                                                                                                                    ('10101010-1010-1010-1010-101010101010', 'Which planet is known as the Red Planet?', NULL, 10, 1, '["Venus", "Earth", "Mars", "Jupiter"]', '["Mars"]', '88888888-8888-8888-8888-888888888888', NOW(), NOW()),
                                                                                                                                    ('11111111-1111-1111-1111-111111111111', 'Select the primary colors.', NULL, 15, 3, '["Red", "Blue", "Green", "Yellow"]', '["Red", "Blue", "Yellow"]', '88888888-8888-8888-8888-888888888888', NOW(), NOW()),
                                                                                                                                    ('12121212-1212-1212-1212-121212121212', 'What is the speed of light?', NULL, 15, 1, '["300,000 km/s", "150,000 km/s", "1,000,000 km/s", "500,000 km/s"]', '["300,000 km/s"]', '88888888-8888-8888-8888-888888888888', NOW(), NOW());


INSERT INTO account (id, first_name, last_name, username, email, password, active, created_at, updated_at)
VALUES ('22222222-2222-2222-2222-222222222222', 'Jane', 'Smith', 'janesmith', 'jane@example.com', 'password', 1, NOW(), NOW());

INSERT INTO result (id, account_id, survey_id, start_time, end_time, duration, total_points, correct_answers, incorrect_answers, created_at)
VALUES
    ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-444444444444', NOW(), NOW(), 3600, 100, 3, 1, NOW());

INSERT INTO answer (id, account_id, question_id, result_id, user_answers, is_correct, points, created_at)
VALUES
    ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', '33333333-3333-3333-3333-333333333333', '["Paris"]', 1, 10, NOW()),
    ('55555555-5555-5555-5555-555555555555', '22222222-2222-2222-2222-222222222222', '66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', '["Python", "Java"]', 1, 15, NOW()),
    ('66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', '77777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', '["11"]', 1, 10, NOW());

INSERT INTO result (id, account_id, survey_id, start_time, end_time, duration, total_points, correct_answers, incorrect_answers, created_at)
VALUES
    ('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', NOW(), NOW(), 3600, 100, 4, 1, NOW());

INSERT INTO answer (id, account_id, question_id, result_id, user_answers, is_correct, points, created_at)
VALUES
    ('77777777-7777-7777-7777-777777777777', '22222222-2222-2222-2222-222222222222', '99999999-9999-9999-9999-999999999999', '44444444-4444-4444-4444-444444444444', '["Deoxyribonucleic Acid"]', 1, 10, NOW()),
    ('88888888-8888-8888-8888-888888888888', '22222222-2222-2222-2222-222222222222', '10101010-1010-1010-1010-101010101010', '44444444-4444-4444-4444-444444444444', '["Mars"]', 1, 10, NOW()),
    ('99999999-9999-9999-9999-999999999999', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', '["Red", "Blue", "Yellow"]', 1, 15, NOW()),
    ('10101010-1010-1010-1010-101010101010', '22222222-2222-2222-2222-222222222222', '12121212-1212-1212-1212-121212121212', '44444444-4444-4444-4444-444444444444', '["300,000 km/s"]', 1, 15, NOW());
