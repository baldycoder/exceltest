-- excel.user_info definition

CREATE TABLE `user_info` (
                             `work_id` bigint NOT NULL,
                             `user_name` varchar(100) NOT NULL,
                             `age` bigint DEFAULT NULL,
                             `workdate` datetime NOT NULL,
                             PRIMARY KEY (`work_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;