INSERT INTO certificatetracker.certificates
(issuer, subject, url, valid_from, valid_to) VALUES 
("test1issuer", "test1subject","https://www.expired.com",
"2023-09-01T04:00:00.000+00:00",
"2023-09-19T04:00:00.000+00:00");
INSERT INTO certificatetracker.certificates
(issuer, subject, url, valid_from, valid_to) VALUES
("test2issuer", "test2subject","https://www.soontoexpire.com",
"2023-09-01T04:00:00.000+00:00",
"2023-09-25T04:00:00.000+00:00");
INSERT INTO certificatetracker.certificates
(issuer, subject, url, valid_from, valid_to) VALUES
("test3issuer", "test3subject","https://www.expireinmonth.com",
"2023-09-01T04:00:00.000+00:00",
"2023-10-25T04:00:00.000+00:00");
INSERT INTO certificatetracker.certificates
(issuer, subject, url, valid_from, valid_to) VALUES
("test4issuer", "test4subject","https://www.lotsoftime.com",
"2023-09-01T04:00:00.000+00:00",
"2024-10-25T04:00:00.000+00:00");