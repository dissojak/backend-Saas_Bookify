-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 22, 2025 at 12:55 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bookify_saas`
--

-- --------------------------------------------------------

--
-- Table structure for table `activation_tokens`
--

CREATE TABLE `activation_tokens` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `activation_tokens`
--

INSERT INTO `activation_tokens` (`id`, `created_at`, `expiry_date`, `token`, `user_id`) VALUES
(8, '2025-10-21 18:19:33.000000', '2025-10-28 18:19:33.000000', 'cc06840b-684c-441d-8cf9-65061e71d44f', 8);

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`id`) VALUES
(10);

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `date` date NOT NULL,
  `end_time` time(6) NOT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `start_time` time(6) NOT NULL,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','NO_SHOW','PENDING') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `businesses`
--

CREATE TABLE `businesses` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','DELETED','DRAFT','INACTIVE','PENDING','SUSPENDED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category_id` bigint(20) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `description` varchar(2000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `businesses`
--

INSERT INTO `businesses` (`id`, `created_at`, `email`, `location`, `name`, `phone`, `status`, `updated_at`, `category_id`, `owner_id`, `description`) VALUES
(19, '2025-10-21 22:07:37.000000', 'contact@stoonbarbershop.com', 'Tunisia,Nabeul,Maamoura - 8013', 'Stoon Barber Shop', '+21680138013', 'DRAFT', '2025-10-21 22:19:43.000000', 7, 9, 'Full-service barber shop offering haircuts, shaves and grooming.');

-- --------------------------------------------------------

--
-- Table structure for table `business_evaluations`
--

CREATE TABLE `business_evaluations` (
  `id` bigint(20) NOT NULL,
  `branding_details` varchar(1000) DEFAULT NULL,
  `branding_score` int(11) DEFAULT NULL,
  `branding_suggestions` varchar(1000) DEFAULT NULL,
  `category_details` varchar(1000) DEFAULT NULL,
  `category_score` int(11) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `description_details` varchar(1000) DEFAULT NULL,
  `description_professionalism_score` int(11) DEFAULT NULL,
  `description_suggestions` varchar(1000) DEFAULT NULL,
  `email_details` varchar(1000) DEFAULT NULL,
  `email_professionalism_score` int(11) DEFAULT NULL,
  `email_suggestions` varchar(1000) DEFAULT NULL,
  `location_details` varchar(1000) DEFAULT NULL,
  `location_score` int(11) DEFAULT NULL,
  `name_details` varchar(1000) DEFAULT NULL,
  `name_professionalism_score` int(11) DEFAULT NULL,
  `name_suggestions` varchar(1000) DEFAULT NULL,
  `overall_score` int(11) DEFAULT NULL,
  `source` varchar(50) DEFAULT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `business_evaluations`
--

INSERT INTO `business_evaluations` (`id`, `branding_details`, `branding_score`, `branding_suggestions`, `category_details`, `category_score`, `created_at`, `description_details`, `description_professionalism_score`, `description_suggestions`, `email_details`, `email_professionalism_score`, `email_suggestions`, `location_details`, `location_score`, `name_details`, `name_professionalism_score`, `name_suggestions`, `overall_score`, `source`, `business_id`) VALUES
(4, 'The branding is somewhat weak due to the poor description. While the name and email are consistent, the description doesn\'t convey a strong brand image.\nAI Overall: 75/100 — The business has a solid foundation with a good name, location, and email. However, the description needs significant improvement to enhance branding and attract customers. Focus on detailing services and creating a more compelling narrative.', 70, NULL, 'The category \'Barber\' aligns well with the name and intended services.', 90, '2025-10-21 22:07:41.000000', 'The description is repetitive and lacks detail. It simply repeats the location information.', 40, 'Stoon Barber Shop offers expert haircuts, styling, and grooming services in Maamoura, Nabeul. Visit us for a fresh look and a relaxing experience.', 'The email address is valid and brand-aligned.', 95, NULL, 'The location is well-formatted, including city, region and postal code. Country is also present.', 95, 'The name is clear and identifies the type of business. \'Stoon\' is unique and memorable.', 85, NULL, 79, 'AI', 19);

-- --------------------------------------------------------

--
-- Table structure for table `business_owners`
--

CREATE TABLE `business_owners` (
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `business_owners`
--

INSERT INTO `business_owners` (`id`) VALUES
(8),
(9);

-- --------------------------------------------------------

--
-- Table structure for table `business_ratings`
--

CREATE TABLE `business_ratings` (
  `id` bigint(20) NOT NULL,
  `business_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `created_at`, `description`, `name`, `updated_at`, `created_by`) VALUES
(7, '2025-10-21 19:54:38.774920', 'Haircuts, shaves and grooming services', 'Barber', NULL, 10);

-- --------------------------------------------------------

--
-- Table structure for table `clients`
--

CREATE TABLE `clients` (
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `clients`
--

INSERT INTO `clients` (`id`) VALUES
(7);

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` bigint(20) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_ref` varchar(255) DEFAULT NULL,
  `booking_id` bigint(20) DEFAULT NULL,
  `subscription_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ratings`
--

CREATE TABLE `ratings` (
  `id` bigint(20) NOT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `date` date NOT NULL,
  `score` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `resources`
--

CREATE TABLE `resources` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  `status` enum('AVAILABLE','HOLIDAY','MAINTENANCE','OUT_OF_ORDER') NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `resource_availabilities`
--

CREATE TABLE `resource_availabilities` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `date` date NOT NULL,
  `end_time` time(6) NOT NULL,
  `start_time` time(6) NOT NULL,
  `status` enum('AVAILABLE','HOLIDAY','MAINTENANCE','OUT_OF_ORDER') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `resource_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `resource_ratings`
--

CREATE TABLE `resource_ratings` (
  `id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `resource_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `resource_reservations`
--

CREATE TABLE `resource_reservations` (
  `id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `resource_availability_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `id` bigint(20) NOT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `rating` int(11) NOT NULL,
  `response` varchar(255) DEFAULT NULL,
  `tenant_id` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `booking_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

CREATE TABLE `services` (
  `id` bigint(20) NOT NULL,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `duration_minutes` int(11) NOT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `tenant_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `service_bookings`
--

CREATE TABLE `service_bookings` (
  `id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `service_id` bigint(20) NOT NULL,
  `staff_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `service_ratings`
--

CREATE TABLE `service_ratings` (
  `id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `service_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `service_staff`
--

CREATE TABLE `service_staff` (
  `service_id` bigint(20) NOT NULL,
  `staff_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `staff`
--

CREATE TABLE `staff` (
  `default_end_time` time(6) DEFAULT NULL,
  `default_start_time` time(6) DEFAULT NULL,
  `start_working_at` date DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `staff_availabilities`
--

CREATE TABLE `staff_availabilities` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `date` date NOT NULL,
  `end_time` time(6) NOT NULL,
  `start_time` time(6) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `staff_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subscriptions`
--

CREATE TABLE `subscriptions` (
  `id` bigint(20) NOT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `end_date` date DEFAULT NULL,
  `plan_name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `start_date` date NOT NULL,
  `status` tinyint(4) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `avatar_url` text DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_reset_expires_at` datetime(6) DEFAULT NULL,
  `password_reset_token` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','BUSINESS_OWNER','CLIENT','STAFF') NOT NULL,
  `status` enum('PENDING','SUSPENDED','VERIFIED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `avatar_url`, `created_at`, `email`, `name`, `password`, `password_reset_expires_at`, `password_reset_token`, `phone_number`, `role`, `status`, `updated_at`) VALUES
(7, 'https://example.com/avatar.jpg', '2025-10-21 17:55:01.000000', 'dissojak@icloud.com', 'disso mac', '$2a$10$acWz4F6Db9TZCnEUOfPVXeHxJS.UQg.7edAzX.Go7wYXgzeIinJ.y', NULL, NULL, '+21623039320', 'CLIENT', 'VERIFIED', '2025-10-21 17:55:37.000000'),
(8, 'https://scontent.ftun10-2.fna.fbcdn.net/v/t39.30808-1/464874378_3427610584209156_912009619605911564_n.jpg?stp=dst-jpg_s480x480_tt6&_nc_cat=102&ccb=1-7&_nc_sid=e99d92&_nc_ohc=EU-okqxFnVsQ7kNvwE3Txnp&_nc_oc=Adn3lpOYHXf5-Bb71gwOgMTFbwih4Zosu_bTivN_53gGACvJwzRvhfriN0vSopg2cZE&_nc_zt=24&_nc_ht=scontent.ftun10-2.fna&_nc_gid=3hUwWQzr4uxml83-WAtCTw&oh=00_AfdlnDpUKzhGD3P2FTV_8EnFhq89XAPNALBnP5Zd8KswZA&oe=68FD93F6', '2025-10-21 18:19:33.000000', 'amir@tousihen.com', 'amir', '$2a$10$9Imf5eYLg/QomZ.QyTQHIeXiGfHRmYbVqDOHxNJxgAUwmYVZNDz6u', NULL, NULL, '+21680138013', 'BUSINESS_OWNER', 'PENDING', NULL),
(9, 'https://scontent.ftun10-2.fna.fbcdn.net/v/t39.30808-1/464874378_3427610584209156_912009619605911564_n.jpg?stp=dst-jpg_s480x480_tt6&_nc_cat=102&ccb=1-7&_nc_sid=e99d92&_nc_ohc=EU-okqxFnVsQ7kNvwE3Txnp&_nc_oc=Adn3lpOYHXf5-Bb71gwOgMTFbwih4Zosu_bTivN_53gGACvJwzRvhfriN0vSopg2cZE&_nc_zt=24&_nc_ht=scontent.ftun10-2.fna&_nc_gid=3hUwWQzr4uxml83-WAtCTw&oh=00_AfdlnDpUKzhGD3P2FTV_8EnFhq89XAPNALBnP5Zd8KswZA&oe=68FD93F6', '2025-10-21 18:20:46.000000', 'amirghodhben2.0@gmail.com', 'amir', '$2a$10$flxLn6g1cs2DgWD080NIs.s.cMWNDx5mro1xfNdGBeS78ShzAAOP.', NULL, NULL, '+21680138013', 'BUSINESS_OWNER', 'VERIFIED', '2025-10-21 18:23:10.000000'),
(10, NULL, '2025-10-21 18:54:12.000000', 'admin@example.com', 'Admin User', '$2a$10$XaAlm5BXUJqtMcW1gcKGGexHoJENCkR2v8rMICQSGzmSMomHC05SW', NULL, NULL, NULL, 'ADMIN', 'VERIFIED', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activation_tokens`
--
ALTER TABLE `activation_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK5jny0xpou62bqdjhkbw1c0qxd` (`token`),
  ADD KEY `fk_activation_token_user` (`user_id`);

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `businesses`
--
ALTER TABLE `businesses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_business_owner` (`owner_id`),
  ADD KEY `fk_business_category` (`category_id`);

--
-- Indexes for table `business_evaluations`
--
ALTER TABLE `business_evaluations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_eval_business` (`business_id`);

--
-- Indexes for table `business_owners`
--
ALTER TABLE `business_owners`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `business_ratings`
--
ALTER TABLE `business_ratings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKdi5ag3hnfia75c38b8n0u9d98` (`business_id`),
  ADD KEY `FKtdll5gr95w9qbdfbn6iwhi4iq` (`client_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`),
  ADD KEY `fk_category_created_by` (`created_by`);

--
-- Indexes for table `clients`
--
ALTER TABLE `clients`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKc52o2b1jkxttngufqp3t7jr3h` (`booking_id`),
  ADD KEY `FKa3xnf2o6mt8cqbewvq2ouq3rq` (`subscription_id`);

--
-- Indexes for table `ratings`
--
ALTER TABLE `ratings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `resources`
--
ALTER TABLE `resources`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKc9hsd0b9wogdgg69ljpnlqiph` (`business_id`);

--
-- Indexes for table `resource_availabilities`
--
ALTER TABLE `resource_availabilities`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1kev4jivfvl370wnxjtrq6efh` (`resource_id`);

--
-- Indexes for table `resource_ratings`
--
ALTER TABLE `resource_ratings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKslhorq9hpv02gchayr7mc3mfn` (`client_id`),
  ADD KEY `FKckiv8u2mpj223y0th29xrost5` (`resource_id`);

--
-- Indexes for table `resource_reservations`
--
ALTER TABLE `resource_reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKo11e3rmva3mpuay02809s2t12` (`client_id`),
  ADD KEY `FKgv5iq0d0eoxctbo01ma39sd8y` (`resource_availability_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK28an517hrxtt2bsg93uefugrm` (`booking_id`);

--
-- Indexes for table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKryfh22ccvq43d77rj8d6nfrk8` (`business_id`);

--
-- Indexes for table `service_bookings`
--
ALTER TABLE `service_bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK5chyffxxdku8kamqv71k0024v` (`client_id`),
  ADD KEY `FK1cyr30xgaheo32v5iha15mvfn` (`service_id`),
  ADD KEY `FKobkxb0byfe0oq2tynu2e85h01` (`staff_id`);

--
-- Indexes for table `service_ratings`
--
ALTER TABLE `service_ratings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK7itor3mywk407puyd871q5giv` (`client_id`),
  ADD KEY `FKkunckmqok9qmy5c2hbvdj4pmb` (`service_id`);

--
-- Indexes for table `service_staff`
--
ALTER TABLE `service_staff`
  ADD KEY `FKdycnsk82nmegdwl9yb4hfja3h` (`staff_id`),
  ADD KEY `FKb3hes1oc5ia6nr0fjvni6kg7j` (`service_id`);

--
-- Indexes for table `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4jgouphkdf3700j8qglgebc0h` (`business_id`);

--
-- Indexes for table `staff_availabilities`
--
ALTER TABLE `staff_availabilities`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKrve8emdy0iof8ltpu2cl6goel` (`staff_id`);

--
-- Indexes for table `subscriptions`
--
ALTER TABLE `subscriptions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKd75uwj5b3erhwwt5flxnevr7o` (`business_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activation_tokens`
--
ALTER TABLE `activation_tokens`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `businesses`
--
ALTER TABLE `businesses`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `business_evaluations`
--
ALTER TABLE `business_evaluations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `resources`
--
ALTER TABLE `resources`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `resource_availabilities`
--
ALTER TABLE `resource_availabilities`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `services`
--
ALTER TABLE `services`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `staff_availabilities`
--
ALTER TABLE `staff_availabilities`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subscriptions`
--
ALTER TABLE `subscriptions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activation_tokens`
--
ALTER TABLE `activation_tokens`
  ADD CONSTRAINT `fk_activation_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `FKanhsicqm3lc8ya77tr7r0je18` FOREIGN KEY (`id`) REFERENCES `users` (`id`);

--
-- Constraints for table `businesses`
--
ALTER TABLE `businesses`
  ADD CONSTRAINT `fk_business_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  ADD CONSTRAINT `fk_business_owner` FOREIGN KEY (`owner_id`) REFERENCES `business_owners` (`id`);

--
-- Constraints for table `business_evaluations`
--
ALTER TABLE `business_evaluations`
  ADD CONSTRAINT `fk_eval_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

--
-- Constraints for table `business_owners`
--
ALTER TABLE `business_owners`
  ADD CONSTRAINT `FKdfif92jyrp4dar8ynvvdtw392` FOREIGN KEY (`id`) REFERENCES `users` (`id`);

--
-- Constraints for table `business_ratings`
--
ALTER TABLE `business_ratings`
  ADD CONSTRAINT `FK3sb0qmu96yiuh6tgh56xa1fn3` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`),
  ADD CONSTRAINT `FKdi5ag3hnfia75c38b8n0u9d98` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`),
  ADD CONSTRAINT `FKtdll5gr95w9qbdfbn6iwhi4iq` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`);

--
-- Constraints for table `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `fk_category_created_by` FOREIGN KEY (`created_by`) REFERENCES `admins` (`id`);

--
-- Constraints for table `clients`
--
ALTER TABLE `clients`
  ADD CONSTRAINT `FK1hgwdp9vl25xl9i7s354sifey` FOREIGN KEY (`id`) REFERENCES `users` (`id`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `FKa3xnf2o6mt8cqbewvq2ouq3rq` FOREIGN KEY (`subscription_id`) REFERENCES `subscriptions` (`id`),
  ADD CONSTRAINT `FKc52o2b1jkxttngufqp3t7jr3h` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

--
-- Constraints for table `resources`
--
ALTER TABLE `resources`
  ADD CONSTRAINT `FKc9hsd0b9wogdgg69ljpnlqiph` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

--
-- Constraints for table `resource_availabilities`
--
ALTER TABLE `resource_availabilities`
  ADD CONSTRAINT `FK1kev4jivfvl370wnxjtrq6efh` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`id`);

--
-- Constraints for table `resource_ratings`
--
ALTER TABLE `resource_ratings`
  ADD CONSTRAINT `FKckiv8u2mpj223y0th29xrost5` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`id`),
  ADD CONSTRAINT `FKgxn9yqmios192clsgokw20fi8` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`),
  ADD CONSTRAINT `FKslhorq9hpv02gchayr7mc3mfn` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`);

--
-- Constraints for table `resource_reservations`
--
ALTER TABLE `resource_reservations`
  ADD CONSTRAINT `FKgv5iq0d0eoxctbo01ma39sd8y` FOREIGN KEY (`resource_availability_id`) REFERENCES `resource_availabilities` (`id`),
  ADD CONSTRAINT `FKn37j36nancpcmt0d3p6u9rxfr` FOREIGN KEY (`id`) REFERENCES `bookings` (`id`),
  ADD CONSTRAINT `FKo11e3rmva3mpuay02809s2t12` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

--
-- Constraints for table `services`
--
ALTER TABLE `services`
  ADD CONSTRAINT `FKryfh22ccvq43d77rj8d6nfrk8` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

--
-- Constraints for table `service_bookings`
--
ALTER TABLE `service_bookings`
  ADD CONSTRAINT `FK1cyr30xgaheo32v5iha15mvfn` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  ADD CONSTRAINT `FK5chyffxxdku8kamqv71k0024v` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
  ADD CONSTRAINT `FK6t4e5id96v16yj88y4vnipeqp` FOREIGN KEY (`id`) REFERENCES `bookings` (`id`),
  ADD CONSTRAINT `FKobkxb0byfe0oq2tynu2e85h01` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`);

--
-- Constraints for table `service_ratings`
--
ALTER TABLE `service_ratings`
  ADD CONSTRAINT `FK7itor3mywk407puyd871q5giv` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
  ADD CONSTRAINT `FKkunckmqok9qmy5c2hbvdj4pmb` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  ADD CONSTRAINT `FKqrgme1bj9kts4vbrsj2xupqdo` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`);

--
-- Constraints for table `service_staff`
--
ALTER TABLE `service_staff`
  ADD CONSTRAINT `FKb3hes1oc5ia6nr0fjvni6kg7j` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  ADD CONSTRAINT `FKdycnsk82nmegdwl9yb4hfja3h` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`);

--
-- Constraints for table `staff`
--
ALTER TABLE `staff`
  ADD CONSTRAINT `FK4jgouphkdf3700j8qglgebc0h` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`),
  ADD CONSTRAINT `FKeicqo66n9913jvyogdij5vnlo` FOREIGN KEY (`id`) REFERENCES `clients` (`id`);

--
-- Constraints for table `staff_availabilities`
--
ALTER TABLE `staff_availabilities`
  ADD CONSTRAINT `FKrve8emdy0iof8ltpu2cl6goel` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`);

--
-- Constraints for table `subscriptions`
--
ALTER TABLE `subscriptions`
  ADD CONSTRAINT `FKd75uwj5b3erhwwt5flxnevr7o` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
