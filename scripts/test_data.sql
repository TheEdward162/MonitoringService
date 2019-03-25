USE applifting_monitoring_service;

INSERT INTO user(user_name, email, access_token) VALUES
('zerotest', 'zero@test.sk', '0')
;

INSERT INTO monitored_endpoint(monitored_interval, name, url, user_id) SELECT
30, "testpoint", "http://example.com", user.id FROM user WHERE user_name = 'zerotest';