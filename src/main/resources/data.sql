-- Clear existing data if needed
DELETE FROM user_friends;
DELETE FROM users;

-- Insert users (passwords are "password" encoded with BCrypt)
INSERT INTO users (id, email, password, user_name, date_of_birth, created_at, updated_at)
VALUES
    (1, 'john@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'johndoe', '1996-01-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'teresa@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'teresalewis', '1992-07-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'bob@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'bobsmith', '1985-03-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add profiles
UPDATE users
SET bio = 'I love coding and hiking', location = 'New York', profile_picture = '/images/john.jpg'
WHERE id = 1;

UPDATE users
SET bio = 'Software engineer and coffee enthusiast', location = 'San Francisco', profile_picture = '/images/teresa.jpg'
WHERE id = 2;

UPDATE users
SET bio = 'Photographer and traveler', location = 'Seattle', profile_picture = '/images/bob.jpg'
WHERE id = 3;

-- Create friendships
INSERT INTO user_friends (user_id, friend_id) VALUES
                                                  (1, 2), -- John and Jane are friends
                                                  (2, 1), -- Both directions for bidirectional friendship
                                                  (1, 3), -- John and Bob are friends
                                                  (3, 1); -- Both directions