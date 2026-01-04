-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jan 04, 2026 at 10:59 PM
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

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`id`, `created_at`, `date`, `end_time`, `notes`, `price`, `start_time`, `status`, `updated_at`) VALUES
(1, '2025-12-20 10:00:00.000000', '2025-12-26', '09:00:00.000000', 'Regular customer, prefers fade style', 6.00, '08:30:00.000000', 'COMPLETED', '2025-12-20 10:00:00.000000'),
(2, '2025-12-20 11:00:00.000000', '2025-12-26', '10:30:00.000000', 'First time client', 6.00, '10:00:00.000000', 'CANCELLED', '2026-01-01 17:54:06.000000'),
(3, '2025-12-20 14:00:00.000000', '2025-12-26', '12:15:00.000000', 'Kids haircut, bring toys', 3.00, '12:00:00.000000', 'NO_SHOW', '2026-01-01 17:54:15.000000'),
(4, '2025-12-21 09:00:00.000000', '2025-12-26', '15:00:00.000000', 'Hot towel shave requested', 7.00, '14:45:00.000000', 'NO_SHOW', '2026-01-01 17:54:19.000000'),
(5, '2025-12-21 10:00:00.000000', '2025-12-27', '09:30:00.000000', 'Haircut and beard trim', 6.00, '09:00:00.000000', 'CONFIRMED', '2025-12-21 10:00:00.000000'),
(6, '2025-12-21 11:00:00.000000', '2025-12-27', '11:00:00.000000', 'Quick trim', 6.00, '10:30:00.000000', 'CONFIRMED', '2025-12-21 11:00:00.000000'),
(7, '2025-12-22 08:00:00.000000', '2025-12-27', '14:30:00.000000', 'Hair coloring appointment', 20.00, '13:30:00.000000', 'CONFIRMED', '2025-12-22 08:00:00.000000'),
(8, '2025-12-22 09:00:00.000000', '2025-12-28', '10:00:00.000000', 'Regular cut', 6.00, '09:30:00.000000', 'CONFIRMED', '2025-12-22 09:00:00.000000'),
(9, '2025-12-22 10:00:00.000000', '2025-12-28', '11:45:00.000000', 'Shave and haircut combo', 7.00, '11:00:00.000000', 'CONFIRMED', '2025-12-22 10:00:00.000000'),
(10, '2025-12-23 08:00:00.000000', '2025-12-30', '08:45:00.000000', 'Early morning appointment', 6.00, '08:15:00.000000', 'NO_SHOW', '2026-01-01 17:54:22.000000'),
(11, '2025-12-23 09:00:00.000000', '2026-01-01', '19:50:00.000000', 'Kids haircut', 3.00, '19:35:00.000000', 'COMPLETED', '2026-01-01 19:29:57.000000'),
(12, '2025-12-23 14:00:00.000000', '2026-01-01', '22:00:00.000000', 'Lunchtime quick cut', 6.00, '21:30:00.000000', 'COMPLETED', '2026-01-02 04:10:39.000000'),
(13, '2025-12-24 08:00:00.000000', '2026-01-01', '19:15:00.000000', 'New Year party prep', 6.00, '18:35:00.536000', 'COMPLETED', '2026-01-01 18:21:30.000000'),
(14, '2025-12-24 09:00:00.000000', '2025-12-31', '10:45:00.000000', 'Hot towel shave for party', 7.00, '10:30:00.000000', 'CONFIRMED', '2025-12-24 09:00:00.000000'),
(15, '2025-12-24 10:00:00.000000', '2025-12-31', '12:00:00.000000', 'Haircut before celebration', 6.00, '11:30:00.000000', 'CONFIRMED', '2025-12-24 10:00:00.000000'),
(16, '2025-12-25 08:00:00.000000', '2026-01-02', '09:00:00.000000', 'Post-holiday trim', 6.00, '08:30:00.000000', 'CANCELLED', '2026-01-02 14:05:18.000000'),
(17, '2025-12-25 09:00:00.000000', '2026-01-02', '11:30:00.000000', 'Fresh start haircut', 6.00, '11:00:00.000000', 'CONFIRMED', '2025-12-25 09:00:00.000000'),
(18, '2025-12-25 10:00:00.000000', '2026-01-03', '10:00:00.000000', 'Regular customer', 6.00, '09:30:00.000000', 'CONFIRMED', '2025-12-25 10:00:00.000000'),
(19, '2025-12-25 11:00:00.000000', '2026-01-03', '14:15:00.000000', 'Hair wash and style', 5.00, '13:50:00.000000', 'CONFIRMED', '2025-12-25 11:00:00.000000'),
(20, '2025-12-25 12:00:00.000000', '2026-01-03', '16:00:00.000000', 'Walk-in appointment', 6.00, '15:30:00.000000', 'CONFIRMED', '2025-12-25 12:00:00.000000'),
(21, '2025-12-26 08:00:00.000000', '2026-01-06', '09:30:00.000000', 'Weekly regular', 6.00, '09:00:00.000000', 'CONFIRMED', '2025-12-26 08:00:00.000000'),
(22, '2025-12-26 09:00:00.000000', '2026-01-06', '11:00:00.000000', 'Haircut and shave', 7.00, '10:30:00.000000', 'CONFIRMED', '2025-12-26 09:00:00.000000'),
(23, '2025-12-27 08:00:00.000000', '2026-01-08', '10:15:00.000000', 'Business meeting prep', 6.00, '09:45:00.000000', 'CONFIRMED', '2025-12-27 08:00:00.000000'),
(24, '2025-12-27 09:00:00.000000', '2026-01-08', '13:00:00.000000', 'Kids haircut', 3.00, '12:45:00.000000', 'CONFIRMED', '2025-12-27 09:00:00.000000'),
(25, '2025-12-27 10:00:00.000000', '2026-01-10', '09:00:00.000000', 'Hot towel shave', 7.00, '08:45:00.000000', 'CONFIRMED', '2025-12-27 10:00:00.000000'),
(26, '2025-12-27 11:00:00.000000', '2026-01-10', '10:30:00.000000', 'Regular haircut', 6.00, '10:00:00.000000', 'CONFIRMED', '2025-12-27 11:00:00.000000'),
(27, '2025-12-27 12:00:00.000000', '2026-01-10', '15:30:00.000000', 'Hair coloring', 20.00, '14:30:00.000000', 'CONFIRMED', '2025-12-27 12:00:00.000000'),
(31, '2026-01-02 22:45:25.000000', '2026-01-08', '13:30:00.000000', '', 6.00, '13:00:00.000000', 'CONFIRMED', NULL),
(32, '2026-01-02 22:46:27.000000', '2026-01-15', '15:00:00.000000', 'sec test', 3.00, '14:45:00.000000', 'CONFIRMED', NULL),
(33, '2026-01-02 22:47:39.000000', '2026-01-10', '13:15:00.000000', NULL, 3.00, '13:00:00.000000', 'CONFIRMED', '2026-01-02 22:49:01.000000'),
(36, '2026-01-02 23:32:25.000000', '2026-01-13', '14:15:00.000000', NULL, 3.00, '14:00:00.000000', 'CONFIRMED', '2026-01-02 23:33:22.000000');

-- --------------------------------------------------------

--
-- Table structure for table `businesses`
--

CREATE TABLE `businesses` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(2000) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','DELETED','DRAFT','INACTIVE','PENDING','SUSPENDED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category_id` bigint(20) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `weekend_day` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `businesses`
--

INSERT INTO `businesses` (`id`, `created_at`, `description`, `email`, `location`, `name`, `phone`, `status`, `updated_at`, `category_id`, `owner_id`, `weekend_day`) VALUES
(1, '2025-11-25 20:15:15.000000', 'Men’s barbershop in Maamoura specializing in precision haircuts, clean fades, expert beard grooming, and classic shaves.', 'contact@stoonbarber.com', 'Rue Habib Bourguiba, Maamoura, Nabeul, Tunisia', 'Stoon Barber ', '+21680138013', 'ACTIVE', '2026-01-02 19:08:50.000000', 2, 1, 'TUESDAY'),
(2, '2025-11-26 20:37:59.000000', 'Modern grooming spot with pro barbers and stylish cuts.', 'hello@freshfade.tn', '45 Avenue Habib Bourguiba, Tunis, Tunisia', 'Stoon Production Studio', '+21650112233', 'DRAFT', '2025-11-26 20:47:25.000000', 1, 6, 'MONDAY'),
(3, '2025-10-15 14:30:00.000000', 'Fine dining Italian restaurant featuring authentic pasta, wood-fired pizza, and seasonal specialties.  Wine bar with 100+ selections.', 'reservations@bellaitalia.fr', '78 Boulevard Saint-Germain, 75005 Paris, France', 'Bella Italia', '+33142567890', 'ACTIVE', '2026-01-03 21:08:33.000000', 1, 8, 'MONDAY'),
(4, '2025-09-20 09:00:00.000000', 'Traditional Tunisian restaurant serving couscous, tagines, brik, and Mediterranean seafood. Family recipes passed down for generations.', 'info@darsarrar.tn', '12 Rue du Pacha, Sidi Bou Said, Tunisia', 'Dar Sarrar', '+21671234567', 'ACTIVE', '2025-12-18 12:30:00.000000', 1, 9, 'SUNDAY'),
(5, '2025-11-01 11:20:00.000000', 'Premium salon specializing in hair coloring, balayage, keratin treatments, and bridal styling. Luxury products and experienced stylists.', 'appointments@glamourhair.com', '156 Avenue Mohammed V, Tunis, Tunisia', 'Glamour Hair Salon', '+21698765432', 'ACTIVE', '2025-12-20 15:00:00.000000', 3, 10, 'SUNDAY'),
(6, '2025-08-10 10:00:00.000000', 'Trendy urban barbershop with vintage vibes.  Specializing in classic cuts, beard trims, and straight razor shaves.  Complimentary coffee. ', 'book@urbancuts.com', '89 Rue de la République, 69002 Lyon, France', 'Urban Cuts Barbershop', '+33478901234', 'ACTIVE', '2025-12-22 09:15:00.000000', 2, 11, 'MONDAY'),
(7, '2025-07-05 16:45:00.000000', 'Authentic Japanese restaurant offering sushi, sashimi, ramen, and teppanyaki. Fresh fish delivered daily.  Omakase available.', 'contact@sakuratokyo.fr', '34 Rue Sainte-Anne, 75001 Paris, France', 'Sakura Tokyo', '+33145678901', 'ACTIVE', '2025-12-19 18:30:00.000000', 1, 12, 'TUESDAY'),
(8, '2025-06-12 13:00:00.000000', 'Modern fitness studio offering personal training, group classes, yoga, pilates, and nutritional coaching. State-of-the-art equipment. ', 'hello@fitlifestudio.tn', '67 Avenue de la Liberté, Tunis, Tunisia', 'FitLife Studio', '+21655443322', 'ACTIVE', '2025-12-21 07:30:00.000000', 4, 13, 'FRIDAY'),
(9, '2025-05-18 15:00:00.000000', 'Family-friendly pizzeria with authentic Neapolitan pizza baked in wood-fired oven.  Casual dining atmosphere with outdoor seating.', 'orders@pizzanapoletana.fr', '45 Avenue Jean Jaurès, 75019 Paris, France', 'Pizza Napoletana', '+33149876543', 'ACTIVE', '2025-12-17 19:00:00.000000', 1, 14, 'MONDAY'),
(10, '2025-04-22 11:30:00.000000', 'Elegant spa offering massages, facials, body treatments, and wellness therapies. Tranquil environment for complete relaxation and rejuvenation.', 'bookings@zenspacenter.tn', '88 Rue de Carthage, La Marsa, Tunisia', 'Zen Spa Center', '+21670112233', 'ACTIVE', '2025-12-16 14:00:00.000000', 3, 15, 'SUNDAY'),
(11, '2025-03-15 09:00:00.000000', 'Professional dental clinic offering general dentistry, cosmetic procedures, orthodontics, and emergency dental care. Modern equipment and experienced team.', 'appointments@dentalcare.fr', '12 Rue de la Santé, 75014 Paris, France', 'Dental Care Clinic', '+33144556677', 'ACTIVE', '2025-12-20 16:30:00.000000', 5, 16, 'SUNDAY'),
(12, '2025-02-10 14:00:00.000000', 'Full-service auto repair shop specializing in engine diagnostics, brake service, oil changes, tire replacement, and general maintenance. ', 'service@autoexpress.tn', '234 Route de Sfax, Tunis, Tunisia', 'Auto Express', '+21698123456', 'ACTIVE', '2025-12-18 10:00:00.000000', 6, 17, 'FRIDAY'),
(13, '2025-01-20 10:30:00.000000', 'Professional photography studio for weddings, portraits, corporate events, and commercial shoots. Experienced photographers with creative vision.', 'info@capturemoments.fr', '67 Rue de Rennes, 75006 Paris, France', 'Capture Moments Photography', '+33156789012', 'ACTIVE', '2025-12-15 11:00:00.000000', 7, 18, 'MONDAY'),
(14, '2025-12-01 13:00:00.000000', 'Luxury nail salon offering manicures, pedicures, gel nails, nail art, and spa treatments. Sterilized tools and premium products.', 'hello@nailglam.tn', '23 Avenue de la République, Sousse, Tunisia', 'Nail Glam Studio', '+21673445566', 'ACTIVE', '2025-12-22 15:00:00.000000', 9, 19, 'SUNDAY'),
(15, '2025-11-15 08:00:00.000000', 'Pet grooming salon providing bathing, haircuts, nail trimming, and styling for dogs and cats. Experienced groomers who love animals.', 'contact@pawsandfur.fr', '89 Boulevard Voltaire, 75011 Paris, France', 'Paws & Fur Grooming', '+33167889900', 'ACTIVE', '2025-12-19 09:00:00.000000', 11, 20, 'MONDAY');

-- --------------------------------------------------------

--
-- Table structure for table `business_clients`
--

CREATE TABLE `business_clients` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `notes` varchar(2000) DEFAULT NULL,
  `phone` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `business_clients`
--

INSERT INTO `business_clients` (`id`, `created_at`, `email`, `name`, `notes`, `phone`, `updated_at`, `business_id`) VALUES
(1, '2026-01-02 22:21:50.000000', NULL, 'hazem ben saria', 'test test', '+21654775034', NULL, 1);

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
(2, 'Assessed consistency between name, email, description, location, category.', 100, NULL, 'Category appears consistent with name/description.', 75, '2025-11-26 20:38:00.000000', 'Checked length, clarity, category/city relevance.', 65, NULL, 'Valid format.', 90, NULL, 'Checked number, street type, postal code, city formatting.', 85, 'Checked length, characters, alignment with category.', 95, NULL, 85, 'HEURISTIC', 2),
(10, 'Assessed consistency between name, email, description, location, category.', 100, NULL, 'Category appears consistent with name/description.', 90, '2026-01-02 18:34:41.000000', 'Checked length, clarity, category/city relevance.', 65, NULL, 'Valid format.', 100, NULL, 'Checked number, street type, postal code, city formatting.', 100, 'Checked length, characters, alignment with category.', 100, NULL, 92, 'HEURISTIC', 1),
(11, 'Assessed consistency between name, email, description, location, category.', 100, NULL, 'Category appears consistent with name/description.', 90, '2026-01-02 18:38:14.000000', 'Checked length, clarity, category/city relevance.', 65, NULL, 'Valid format.', 100, NULL, 'Checked number, street type, postal code, city formatting.', 100, 'Checked length, characters, alignment with category.', 100, NULL, 92, 'HEURISTIC', 1),
(12, 'Checked name, email, and description consistency.', 50, NULL, 'Checked alignment of name/description with selected category.', 50, '2026-01-02 18:46:14.000000', 'Assessed grammar, clarity, tone, and content.', 50, NULL, 'Checked format validity and brand alignment.', 50, NULL, 'Evaluated address clarity and standardized format.', 50, 'The name \"Stoon Barber\" is clear, professional, and', 90, NULL, 57, 'AI', 1),
(13, 'Checked name, email, and description consistency.', 50, NULL, 'Checked alignment of name/description with selected category.', 50, '2026-01-02 18:55:56.000000', 'Assessed grammar, clarity, tone, and content.', 50, NULL, 'Checked format validity and brand alignment.', 50, NULL, 'Evaluated address clarity and standardized format.', 50, 'The business name is exceptionally clear, immediately communicating its services and target', 98, NULL, 58, 'AI', 1),
(14, 'Checked name, email, and description consistency.', 50, NULL, 'Checked alignment of name/description with selected category.', 50, '2026-01-02 19:03:03.000000', 'Assessed grammar, clarity, tone, and content.', 50, NULL, 'Checked format validity and brand alignment.', 50, NULL, 'Full street address with city and country.', 95, 'Clear and descriptive business name.', 95, NULL, 65, 'AI', 1),
(15, 'Checked name, email, and description consistency.', 50, NULL, 'Checked alignment of name/description with selected category.', 50, '2026-01-02 19:08:01.000000', 'Assessed grammar, clarity, tone, and content.', 50, NULL, 'Checked format validity and brand alignment.', 50, NULL, 'Full street, city, and country provided.', 98, 'Name is provided and not gibberish.', 95, NULL, 65, 'AI', 1),
(16, 'Checked name, email, and description consistency.', 50, NULL, 'Checked alignment of name/description with selected category.', 50, '2026-01-02 19:09:27.000000', 'Assessed grammar, clarity, tone, and content.', 50, NULL, 'Checked format validity and brand alignment.', 50, NULL, 'Evaluated address clarity and standardized format.', 50, 'Name is provided and not empty.', 95, NULL, 57, 'AI', 1),
(17, 'Checked name, email, and description consistency.', 50, NULL, 'test', 98, '2026-01-02 19:12:37.000000', 'test', 95, NULL, 'test', 98, NULL, 'test', 98, 'test', 95, NULL, 89, 'AI', 1),
(18, 'Assessed consistency between name, email, description, location, category.', 90, NULL, 'Category appears consistent with name/description.', 90, '2026-01-02 19:41:07.000000', 'Checked length, clarity, category/city relevance.', 75, NULL, 'Valid format.', 100, NULL, 'Checked number, street type, postal code, city formatting.\nSuggestion: Use format: \'123 Rue de Paris, 75001 Paris, FR\'. Include street number, type, postal code, city, country.', 65, 'Checked length, characters, alignment with category.', 100, NULL, 87, 'HEURISTIC', 1),
(19, '3+ images (4 images).\nAI Overall: 96/100 — Excellent profile with strong scores across all categories, particularly in branding and location.', 98, NULL, 'Matches business (barber).', 98, '2026-01-02 20:06:37.000000', 'Describes services/business.', 95, NULL, 'Valid email with a custom domain (stoonbarber.com).', 98, NULL, 'Has street, city, and country.', 98, 'Name exists and is not empty.', 95, NULL, 97, 'AI', 1);

-- --------------------------------------------------------

--
-- Table structure for table `business_images`
--

CREATE TABLE `business_images` (
  `id` bigint(20) NOT NULL,
  `display_order` int(11) DEFAULT NULL,
  `image_url` varchar(500) NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  `business_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `business_images`
--

INSERT INTO `business_images` (`id`, `display_order`, `image_url`, `uploaded_at`, `business_id`) VALUES
(1, 1, 'https://images.pexels.com/photos/1813272/pexels-photo-1813272.jpeg?cs=srgb&dl=pexels-thgusstavo-1813272.jpg&fm=jpg', '2025-11-25 20:21:14.000000', 1),
(2, 0, 'https://img.freepik.com/free-photo/barber-styling-beard-man_23-2147778882.jpg?semt=ais_hybrid&w=740&q=80', '2025-11-25 20:21:14.000000', 2),
(3, 3, 'https://images.pexels.com/photos/3998421/pexels-photo-3998421.jpeg', '2025-11-25 20:22:00.000000', 1),
(4, 2, 'https://images.pexels.com/photos/1319460/pexels-photo-1319460.jpeg', '2025-11-25 20:23:00.000000', 1),
(5, 0, 'https://images.pexels.com/photos/897263/pexels-photo-897263.jpeg', '2025-11-25 20:24:00.000000', 1),
(6, 1, 'https://images.pexels.com/photos/1570807/pexels-photo-1570807.jpeg', '2025-11-26 20:40:00.000000', 2),
(7, 2, 'https://images.pexels.com/photos/3992876/pexels-photo-3992876.jpeg', '2025-11-26 20:41:00.000000', 2),
(8, 0, 'https://images.pexels.com/photos/941861/pexels-photo-941861.jpeg', '2025-10-15 15:00:00.000000', 3),
(9, 1, 'https://images.pexels.com/photos/1579739/pexels-photo-1579739.jpeg', '2025-10-15 15:01:00.000000', 3),
(10, 2, 'https://images.pexels.com/photos/1438672/pexels-photo-1438672.jpeg', '2025-10-15 15:02:00.000000', 3),
(11, 3, 'https://images.pexels.com/photos/2147491/pexels-photo-2147491.jpeg', '2025-10-15 15:03:00.000000', 3),
(12, 4, 'https://images.pexels.com/photos/1251198/pexels-photo-1251198.jpeg', '2025-10-15 15:04:00.000000', 3),
(13, 0, 'https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg', '2025-09-20 09:30:00.000000', 4),
(14, 1, 'https://images.pexels.com/photos/1449773/pexels-photo-1449773.jpeg', '2025-09-20 09:31:00.000000', 4),
(15, 2, 'https://images.pexels.com/photos/8879576/pexels-photo-8879576.jpeg', '2025-09-20 09:32:00.000000', 4),
(16, 3, 'https://images.pexels.com/photos/4916237/pexels-photo-4916237.jpeg', '2025-09-20 09:33:00.000000', 4),
(17, 0, 'https://images.pexels.com/photos/3993449/pexels-photo-3993449.jpeg', '2025-11-01 11:45:00.000000', 5),
(18, 1, 'https://images.pexels.com/photos/3065209/pexels-photo-3065209.jpeg', '2025-11-01 11:46:00.000000', 5),
(19, 2, 'https://images.pexels.com/photos/3065171/pexels-photo-3065171.jpeg', '2025-11-01 11:47:00.000000', 5),
(20, 3, 'https://images.pexels.com/photos/3992870/pexels-photo-3992870.jpeg', '2025-11-01 11:48:00.000000', 5),
(21, 4, 'https://images.pexels.com/photos/3993446/pexels-photo-3993446.jpeg', '2025-11-01 11:49:00.000000', 5),
(22, 0, 'https://images.pexels.com/photos/897263/pexels-photo-897263.jpeg', '2025-08-10 10:30:00.000000', 6),
(23, 1, 'https://images.pexels.com/photos/1319459/pexels-photo-1319459.jpeg', '2025-08-10 10:31:00.000000', 6),
(24, 2, 'https://images.pexels.com/photos/1805600/pexels-photo-1805600.jpeg', '2025-08-10 10:32:00.000000', 6),
(25, 0, 'https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg', '2025-07-05 17:00:00.000000', 7),
(26, 1, 'https://images.pexels.com/photos/2635038/pexels-photo-2635038.jpeg', '2025-07-05 17:01:00.000000', 7),
(27, 2, 'https://images.pexels.com/photos/1907227/pexels-photo-1907227.jpeg', '2025-07-05 17:02:00.000000', 7),
(28, 3, 'https://images.pexels.com/photos/357756/pexels-photo-357756.jpeg', '2025-07-05 17:03:00.000000', 7),
(29, 0, 'https://images.pexels.com/photos/1954524/pexels-photo-1954524.jpeg', '2025-06-12 13:30:00.000000', 8),
(30, 1, 'https://images.pexels.com/photos/703012/pexels-photo-703012.jpeg', '2025-06-12 13:31:00.000000', 8),
(31, 2, 'https://images.pexels.com/photos/3822668/pexels-photo-3822668.jpeg', '2025-06-12 13:32:00.000000', 8),
(32, 3, 'https://images.pexels.com/photos/4056723/pexels-photo-4056723.jpeg', '2025-06-12 13:33:00.000000', 8),
(33, 4, 'https://images.pexels.com/photos/416778/pexels-photo-416778.jpeg', '2025-06-12 13:34:00.000000', 8),
(34, 0, 'https://images.pexels.com/photos/1653877/pexels-photo-1653877.jpeg', '2025-05-18 15:30:00.000000', 9),
(35, 1, 'https://images.pexels.com/photos/2147491/pexels-photo-2147491.jpeg', '2025-05-18 15:31:00.000000', 9),
(36, 2, 'https://images.pexels.com/photos/365459/pexels-photo-365459.jpeg', '2025-05-18 15:32:00.000000', 9),
(37, 0, 'https://images.pexels.com/photos/3757657/pexels-photo-3757657.jpeg', '2025-04-22 12:00:00.000000', 10),
(38, 1, 'https://images.pexels.com/photos/3865618/pexels-photo-3865618.jpeg', '2025-04-22 12:01:00.000000', 10),
(39, 2, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', '2025-04-22 12:02:00.000000', 10),
(40, 3, 'https://images.pexels.com/photos/3865619/pexels-photo-3865619.jpeg', '2025-04-22 12:03:00.000000', 10),
(41, 0, 'https://images.pexels.com/photos/3845653/pexels-photo-3845653.jpeg', '2025-03-15 09:30:00.000000', 11),
(42, 1, 'https://images.pexels.com/photos/305565/pexels-photo-305565.jpeg', '2025-03-15 09:31:00.000000', 11),
(43, 0, 'https://images.pexels.com/photos/3806288/pexels-photo-3806288.jpeg', '2025-02-10 14:30:00.000000', 12),
(44, 1, 'https://images.pexels.com/photos/13065690/pexels-photo-13065690.jpeg', '2025-02-10 14:31:00.000000', 12),
(45, 2, 'https://images.pexels.com/photos/279949/pexels-photo-279949.jpeg', '2025-02-10 14:32:00.000000', 12),
(46, 0, 'https://images.pexels.com/photos/1024966/pexels-photo-1024966.jpeg', '2025-01-20 11:30:00.000000', 13),
(47, 1, 'https://images.pexels.com/photos/1648387/pexels-photo-1648387.jpeg', '2025-01-20 11:31:00.000000', 13),
(48, 2, 'https://images.pexels.com/photos/3760809/pexels-photo-3760809.jpeg', '2025-01-20 11:32:00.000000', 13),
(49, 3, 'https://images.pexels.com/photos/1446161/pexels-photo-1446161.jpeg', '2025-01-20 11:33:00.000000', 13),
(50, 0, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', '2025-12-01 14:30:00.000000', 14),
(51, 1, 'https://images.pexels.com/photos/3997374/pexels-photo-3997374.jpeg', '2025-12-01 14:31:00.000000', 14),
(52, 2, 'https://images.pexels.com/photos/3997989/pexels-photo-3997989.jpeg', '2025-12-01 14:32:00.000000', 14),
(53, 3, 'https://images.pexels.com/photos/1477900/pexels-photo-1477900.jpeg', '2025-12-01 14:33:00.000000', 14),
(54, 4, 'https://images.pexels.com/photos/1615798/pexels-photo-1615798.jpeg', '2025-12-01 14:34:00.000000', 14),
(55, 0, 'https://images.pexels.com/photos/5731890/pexels-photo-5731890.jpeg', '2025-11-15 09:30:00.000000', 15),
(56, 1, 'https://images.pexels.com/photos/7210754/pexels-photo-7210754.jpeg', '2025-11-15 09:31:00.000000', 15),
(57, 2, 'https://images.pexels.com/photos/4587998/pexels-photo-4587998.jpeg', '2025-11-15 09:32:00.000000', 15);

-- --------------------------------------------------------

--
-- Table structure for table `business_ratings`
--

CREATE TABLE `business_ratings` (
  `id` bigint(20) NOT NULL,
  `business_id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `business_ratings`
--

INSERT INTO `business_ratings` (`id`, `business_id`, `client_id`) VALUES
(1, 1, 16),
(2, 1, 17),
(3, 1, 18),
(4, 1, 19),
(5, 3, 20),
(6, 3, 21),
(7, 3, 22),
(8, 3, 23),
(9, 4, 24),
(10, 4, 25),
(11, 4, 16),
(12, 5, 17),
(13, 5, 18),
(14, 5, 19),
(15, 6, 20),
(16, 6, 21),
(17, 6, 22),
(18, 7, 23),
(19, 7, 24),
(20, 7, 25),
(21, 8, 16),
(22, 8, 17),
(23, 8, 18),
(24, 9, 19),
(25, 9, 20),
(26, 9, 21),
(27, 10, 22),
(28, 10, 23),
(29, 10, 24),
(30, 11, 25),
(31, 11, 16),
(32, 11, 17),
(33, 12, 18),
(34, 12, 19),
(35, 12, 20),
(36, 13, 21),
(37, 13, 22),
(38, 13, 23),
(39, 14, 24),
(40, 14, 25),
(41, 14, 16),
(42, 15, 17),
(43, 15, 18),
(44, 15, 19),
(46, 1, 3);

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
  `created_by` bigint(20) NOT NULL,
  `icon` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `created_at`, `description`, `name`, `updated_at`, `created_by`, `icon`) VALUES
(1, '2025-11-25 20:11:12.000000', 'Food and dining establishments', 'Restaurants', NULL, 4, '🍽️'),
(2, '2025-11-25 20:12:36.000000', 'cuts , barber and dining establishments.', 'barber', '2026-01-03 19:59:48.000000', 4, '✂️'),
(3, '2025-10-10 10:00:00.000000', 'Beauty salons, hair styling, spas, and wellness centers', 'Beauty & Spa', NULL, 4, '💇'),
(4, '2025-10-10 10:05:00.000000', 'Fitness centers, gyms, yoga studios, and personal training', 'Fitness & Wellness', NULL, 4, '💪'),
(5, '2025-10-10 10:10:00.000000', 'Medical clinics, dental offices, and healthcare providers', 'Healthcare', NULL, 4, '🏥'),
(6, '2025-10-10 10:15:00.000000', 'Auto repair shops, car washes, and maintenance services', 'Automotive', NULL, 4, '🚗'),
(7, '2025-10-10 10:20:00.000000', 'Photography studios, videography, and event coverage', 'Photography', NULL, 4, '📸'),
(8, '2025-10-10 10:25:00.000000', 'Massage therapy, physiotherapy, and body treatments', 'Massage & Therapy', NULL, 4, '💆'),
(9, '2025-10-10 10:30:00.000000', 'Nail salons, manicure, pedicure, and nail art', 'Nail Salon', NULL, 4, '💅'),
(10, '2025-10-10 10:35:00.000000', 'Tattoo studios, piercing, and body art.', 'Tattoo & Piercing', '2026-01-03 19:59:39.000000', 4, '🎨'),
(11, '2025-10-10 10:40:00.000000', 'Pet grooming, veterinary clinics, and pet care', 'Pet Services', NULL, 4, '🐾'),
(12, '2025-10-10 10:45:00.000000', 'Language schools, tutoring, and educational services', 'Education & Tutoring', NULL, 4, '📚'),
(13, '2025-10-10 10:50:00.000000', 'Music lessons, dance classes, and performing arts', 'Music & Dance', NULL, 4, '🎵'),
(14, '2025-10-10 10:55:00.000000', 'Legal consulting, law firms, and legal services', 'Legal Services', NULL, 4, '⚖️'),
(15, '2025-10-10 11:00:00.000000', 'Accounting, tax preparation, and financial consulting', 'Financial Services', NULL, 4, '💰'),
(16, '2025-10-10 11:05:00.000000', 'Event planning, wedding planning, and coordination', 'Event Planning', NULL, 4, '🎉'),
(17, '2025-10-10 11:10:00.000000', 'Home cleaning, office cleaning, and maintenance', 'Cleaning Services', NULL, 4, '🧹'),
(18, '2025-10-10 11:15:00.000000', 'Plumbing, electrical, carpentry, and home repairs', 'Home Services', NULL, 4, '🔧'),
(19, '2025-10-10 11:20:00.000000', 'Hotels, bed & breakfast, and accommodation', 'Accommodation', NULL, 4, '🏨'),
(20, '2025-10-10 11:25:00.000000', 'Travel agencies, tour guides, and tourism services', 'Travel & Tourism', NULL, 4, '✈️');

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

--
-- Dumping data for table `ratings`
--

INSERT INTO `ratings` (`id`, `comment`, `created_at`, `date`, `score`) VALUES
(1, 'Excellent service! The barber really knows what he is doing. Very professional and friendly. ', '2025-12-10 11:00:00.000000', '2025-12-10', 1),
(2, 'Good haircut but had to wait 20 minutes even with an appointment. ', '2025-12-12 14:30:00.000000', '2025-12-12', 4),
(3, 'Best barbershop in Paris! Always consistent quality.  Highly recommend.', '2025-12-15 16:00:00.000000', '2025-12-15', 5),
(4, 'My son loved his haircut! The staff was very patient with children.', '2025-12-18 10:00:00.000000', '2025-12-18', 2),
(5, 'Amazing food! The pasta was homemade and delicious. Great wine selection too.', '2025-11-20 20:30:00.000000', '2025-11-20', 5),
(6, 'Good Italian restaurant but a bit pricey. Service was excellent though.', '2025-12-05 21:00:00.000000', '2025-12-05', 4),
(7, 'The pizza was incredible! Best I have had outside of Italy.', '2025-12-10 19:30:00.000000', '2025-12-10', 5),
(8, 'Nice atmosphere and authentic taste. Will definitely come back.', '2025-12-18 20:00:00.000000', '2025-12-18', 4),
(9, 'Authentic Tunisian cuisine. The couscous was fantastic.  Friendly staff and nice ambiance.', '2025-11-25 21:00:00.000000', '2025-11-25', 5),
(10, 'Traditional recipes that taste like home. Highly recommended for authentic experience.', '2025-12-08 20:30:00.000000', '2025-12-08', 2),
(11, 'Good food but service was a bit slow. Worth the wait though.', '2025-12-15 19:00:00.000000', '2025-12-15', 4),
(12, 'Great experience!  Loved my hair color.  The stylist was very skilled and listened to exactly what I wanted.', '2025-12-12 16:00:00.000000', '2025-12-12', 5),
(13, 'Professional salon with high-quality products. A bit expensive but worth it. ', '2025-12-16 17:00:00.000000', '2025-12-16', 2),
(14, 'Best balayage I have ever had! The staff is talented and friendly.', '2025-12-20 15:30:00.000000', '2025-12-20', 5),
(15, 'Perfect haircut every time. These guys are pros! ', '2025-11-20 11:00:00.000000', '2025-11-20', 5),
(16, 'Great atmosphere and skilled barbers. The complimentary coffee is a nice touch. ', '2025-12-05 12:00:00.000000', '2025-12-05', 2),
(17, 'Good service but prices have gone up recently. ', '2025-12-18 14:00:00.000000', '2025-12-18', 4),
(18, 'Outstanding sushi! Fresh fish and authentic Japanese flavors. ', '2025-11-28 21:00:00.000000', '2025-11-28', 5),
(19, 'The omakase experience was incredible. Chef is very talented.', '2025-12-08 22:00:00.000000', '2025-12-08', 3),
(20, 'Good ramen but portions could be bigger for the price.', '2025-12-15 20:00:00.000000', '2025-12-15', 4),
(21, 'Amazing personal trainer! Helped me reach my fitness goals.', '2025-11-30 09:00:00.000000', '2025-11-30', 5),
(22, 'Great yoga classes.  The instructor is patient and knowledgeable.', '2025-12-10 08:00:00.000000', '2025-12-10', 3),
(23, 'Good gym with modern equipment. Gets crowded during peak hours.', '2025-12-18 18:00:00.000000', '2025-12-18', 4),
(24, 'Authentic Neapolitan pizza!  The wood-fired oven makes all the difference.', '2025-12-02 20:00:00.000000', '2025-12-02', 5),
(25, 'Family-friendly atmosphere. Kids loved the pizza. ', '2025-12-12 19:00:00.000000', '2025-12-12', 5),
(26, 'Good pizza but nothing extraordinary. Decent prices though.', '2025-12-19 18:30:00.000000', '2025-12-19', 3),
(27, 'Most relaxing massage I have ever had. Tranquil environment.', '2025-12-05 15:00:00.000000', '2025-12-05', 5),
(28, 'Excellent facial treatment. My skin feels amazing! ', '2025-12-14 16:00:00.000000', '2025-12-14', 5),
(29, 'Good spa but appointment times could be more flexible.', '2025-12-20 14:00:00.000000', '2025-12-20', 4),
(30, 'Professional and painless dental cleaning. Highly recommend Dr. Martin.', '2025-11-25 10:00:00.000000', '2025-11-25', 5),
(31, 'Great teeth whitening results!  Staff is very friendly.', '2025-12-08 11:00:00.000000', '2025-12-08', 5),
(32, 'Good dental care but waiting room could be more comfortable.', '2025-12-17 09:00:00.000000', '2025-12-17', 4),
(33, 'Fast and reliable service. They fixed my brakes quickly.', '2025-12-01 16:00:00.000000', '2025-12-01', 5),
(34, 'Honest mechanics who do not overcharge. Will come back. ', '2025-12-10 15:00:00.000000', '2025-12-10', 5),
(35, 'Good service but took longer than estimated. ', '2025-12-18 14:00:00.000000', '2025-12-18', 4),
(36, 'Our wedding photos are absolutely stunning! Worth every penny.', '2025-11-15 12:00:00.000000', '2025-11-15', 5),
(37, 'Professional photographer with great creative vision. ', '2025-12-05 13:00:00.000000', '2025-12-05', 5),
(38, 'Good family portraits.  Could have been faster though.', '2025-12-15 11:00:00.000000', '2025-12-15', 4),
(39, 'Best manicure ever! The nail art is gorgeous. ', '2025-12-08 16:00:00.000000', '2025-12-08', 5),
(40, 'Very clean salon with professional staff. Gel nails lasted 3 weeks! ', '2025-12-16 17:00:00.000000', '2025-12-16', 5),
(41, 'Good pedicure but a bit expensive for what you get.', '2025-12-20 15:00:00.000000', '2025-12-20', 4),
(42, 'My dog looks amazing! The groomers are patient and gentle.', '2025-12-03 10:00:00.000000', '2025-12-03', 5),
(43, 'Excellent pet grooming service. My cat actually enjoyed it!', '2025-12-12 11:00:00.000000', '2025-12-12', 5),
(44, 'Good grooming but wish they had more appointment slots available.', '2025-12-19 10:00:00.000000', '2025-12-19', 4),
(45, 'this was stoon testing', '2025-12-28 22:39:14.000000', '2025-12-29', 5),
(46, NULL, '2025-12-28 22:40:10.000000', '2025-12-29', 5);

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
  `business_rating` int(11) DEFAULT NULL,
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
  `business_id` bigint(20) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `services`
--

INSERT INTO `services` (`id`, `active`, `created_at`, `description`, `duration_minutes`, `image_url`, `name`, `price`, `tenant_id`, `updated_at`, `business_id`, `created_by`) VALUES
(1, b'1', '2025-11-25 20:32:19.000000', 'Basic cut', 30, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMOP5LizXxUSD4y4YF2HW6HyG3xTiOKnLb8g&s', 'Haircut', 6.00, NULL, NULL, 1, 1),
(6, b'1', '2025-11-25 23:47:48.000000', 'Full hair coloring or highlights', 60, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUoPcLKKLTG5n9l4LpDZoCX1Ddpe-FTGHo4A&s', 'Hair Coloring', 50.00, NULL, '2026-01-02 23:39:58.000000', 1, 2),
(7, b'1', '2025-11-26 02:04:13.000000', 'Classic shave with hot towel treatment', 15, 'https://images.squarespace-cdn.com/content/v1/6499daf94cf67b3cc46ceb7c/15abf3c3-8b87-4aad-8f2f-ff6748cd353b/hot+towel+shave+dublin.jpg', 'Hot Towel Shave', 7.00, NULL, '2026-01-01 21:19:13.000000', 1, 2),
(8, b'1', '2025-11-26 02:12:29.000000', 'Gentle haircut for children under 12', 20, 'https://i0.wp.com/mancaveformen.com/wp-content/uploads/2024/07/Kids-Haircuts-Coral-Gables-Florida-Best-Haircuts-Coral-Gables.jpeg?resize=600%2C600', 'Kids Haircut', 3.00, NULL, '2026-01-01 21:20:00.000000', 1, 2),
(9, b'1', '2025-11-26 02:13:04.000000', 'Shampoo, conditioner, and full styling', 25, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTruToe50QbCP2q4B3KlU6txv2ChbfeLVGQSw&s', 'Hair Wash & Style', 5.00, NULL, NULL, 1, 5),
(10, b'1', '2025-10-15 15:00:00.000000', 'Fresh house-made pasta with choice of sauce', 45, 'https://images.pexels.com/photos/1438672/pexels-photo-1438672.jpeg', 'Pasta Dish', 14.00, NULL, NULL, 3, 8),
(11, b'1', '2025-10-15 15:05:00.000000', 'Wood-fired Margherita pizza with fresh mozzarella', 30, 'https://images.pexels.com/photos/2147491/pexels-photo-2147491.jpeg', 'Pizza Margherita', 12.00, NULL, NULL, 3, 8),
(12, b'1', '2025-10-15 15:10:00.000000', 'Classic Italian tiramisu dessert', 15, 'https://images.pexels.com/photos/6210874/pexels-photo-6210874.jpeg', 'Tiramisu', 7.00, NULL, NULL, 3, 8),
(13, b'1', '2025-09-20 10:00:00.000000', 'Traditional Tunisian couscous with lamb and vegetables', 60, 'https://images.pexels.com/photos/8879576/pexels-photo-8879576.jpeg', 'Couscous', 16.00, NULL, NULL, 4, 9),
(14, b'1', '2025-09-20 10:05:00.000000', 'Crispy Tunisian brik with egg and tuna', 20, 'https://images.pexels.com/photos/4916237/pexels-photo-4916237.jpeg', 'Brik', 5.00, NULL, NULL, 4, 9),
(15, b'1', '2025-09-20 10:10:00.000000', 'Grilled Mediterranean sea bass with herbs', 50, 'https://images.pexels.com/photos/262959/pexels-photo-262959.jpeg', 'Grilled Fish', 20.00, NULL, NULL, 4, 9),
(16, b'1', '2025-11-01 12:00:00.000000', 'Premium balayage hair coloring technique', 120, 'https://images.pexels.com/photos/3065171/pexels-photo-3065171.jpeg', 'Balayage', 85.00, NULL, NULL, 5, 10),
(17, b'1', '2025-11-01 12:05:00.000000', 'Brazilian keratin treatment for smooth hair', 150, 'https://images.pexels.com/photos/3993449/pexels-photo-3993449.jpeg', 'Keratin Treatment', 120.00, NULL, NULL, 5, 10),
(18, b'1', '2025-11-01 12:10:00.000000', 'Complete bridal hair and makeup package', 180, 'https://images.pexels.com/photos/3065209/pexels-photo-3065209.jpeg', 'Bridal Package', 200.00, NULL, NULL, 5, 10),
(19, b'1', '2025-11-01 12:15:00.000000', 'Women haircut with styling', 45, 'https://images.pexels.com/photos/3993449/pexels-photo-3993449.jpeg', 'Women Haircut', 35.00, NULL, NULL, 5, 10),
(20, b'1', '2025-08-10 11:00:00.000000', 'Classic gentleman haircut', 40, 'https://images.pexels.com/photos/897263/pexels-photo-897263.jpeg', 'Classic Cut', 12.00, NULL, NULL, 6, 11),
(21, b'1', '2025-08-10 11:05:00.000000', 'Straight razor shave with hot towel', 30, 'https://images.pexels.com/photos/1570807/pexels-photo-1570807.jpeg', 'Straight Razor Shave', 15.00, NULL, NULL, 6, 11),
(22, b'1', '2025-08-10 11:10:00.000000', 'Beard trim and shaping', 20, 'https://images.pexels.com/photos/1570807/pexels-photo-1570807.jpeg', 'Beard Trim', 8.00, NULL, NULL, 6, 11),
(23, b'1', '2025-08-10 11:15:00.000000', 'Modern fade haircut', 35, 'https://images.pexels.com/photos/1805600/pexels-photo-1805600.jpeg', 'Fade Cut', 14.00, NULL, NULL, 6, 11),
(24, b'1', '2025-07-05 17:30:00.000000', 'Assorted fresh sushi and sashimi platter', 30, 'https://images.pexels.com/photos/2098085/pexels-photo-2098085.jpeg', 'Sushi Platter', 28.00, NULL, NULL, 7, 12),
(25, b'1', '2025-07-05 17:35:00.000000', 'Traditional Japanese ramen with pork', 40, 'https://images.pexels.com/photos/1907227/pexels-photo-1907227.jpeg', 'Ramen', 15.00, NULL, NULL, 7, 12),
(26, b'1', '2025-07-05 17:40:00.000000', 'Chef selection omakase experience', 90, 'https://images.pexels.com/photos/2635038/pexels-photo-2635038.jpeg', 'Omakase', 85.00, NULL, NULL, 7, 12),
(27, b'1', '2025-06-12 14:00:00.000000', 'One-on-one personal training session', 60, 'https://images.pexels.com/photos/1954524/pexels-photo-1954524.jpeg', 'Personal Training', 45.00, NULL, NULL, 8, 13),
(28, b'1', '2025-06-12 14:05:00.000000', 'Group yoga class for all levels', 60, 'https://images.pexels.com/photos/3822668/pexels-photo-3822668.jpeg', 'Yoga Class', 15.00, NULL, NULL, 8, 13),
(29, b'1', '2025-06-12 14:10:00.000000', 'High-intensity interval training class', 45, 'https://images.pexels.com/photos/703012/pexels-photo-703012.jpeg', 'HIIT Class', 18.00, NULL, NULL, 8, 13),
(30, b'1', '2025-06-12 14:15:00.000000', 'Pilates mat class', 60, 'https://images.pexels.com/photos/4056723/pexels-photo-4056723.jpeg', 'Pilates Class', 16.00, NULL, NULL, 8, 13),
(31, b'1', '2025-05-18 16:00:00.000000', 'Classic Neapolitan pizza', 25, 'https://images.pexels.com/photos/2147491/pexels-photo-2147491.jpeg', 'Napoletana Pizza', 13.00, NULL, NULL, 9, 14),
(32, b'1', '2025-05-18 16:05:00.000000', 'Four cheese pizza', 25, 'https://images.pexels.com/photos/365459/pexels-photo-365459.jpeg', 'Quattro Formaggi', 15.00, NULL, NULL, 9, 14),
(33, b'1', '2025-05-18 16:10:00.000000', 'Calzone filled with ham and cheese', 30, 'https://images.pexels.com/photos/2147491/pexels-photo-2147491.jpeg', 'Calzone', 12.00, NULL, NULL, 9, 14),
(34, b'1', '2025-04-22 12:00:00.000000', 'Swedish relaxation massage', 60, 'https://images.pexels.com/photos/3757657/pexels-photo-3757657.jpeg', 'Swedish Massage', 70.00, NULL, NULL, 10, 15),
(35, b'1', '2025-04-22 12:05:00.000000', 'Deep tissue therapeutic massage', 75, 'https://images.pexels.com/photos/3865618/pexels-photo-3865618.jpeg', 'Deep Tissue Massage', 85.00, NULL, NULL, 10, 15),
(36, b'1', '2025-04-22 12:10:00.000000', 'Facial treatment with organic products', 60, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', 'Organic Facial', 65.00, NULL, NULL, 10, 15),
(37, b'1', '2025-04-22 12:15:00.000000', 'Hot stone massage therapy', 90, 'https://images.pexels.com/photos/3865618/pexels-photo-3865618.jpeg', 'Hot Stone Massage', 95.00, NULL, NULL, 10, 15),
(38, b'1', '2025-03-15 10:00:00.000000', 'General dental checkup and cleaning', 45, 'https://images.pexels.com/photos/3845653/pexels-photo-3845653.jpeg', 'Dental Checkup', 80.00, NULL, NULL, 11, 16),
(39, b'1', '2025-03-15 10:05:00.000000', 'Teeth whitening treatment', 60, 'https://images.pexels.com/photos/305565/pexels-photo-305565.jpeg', 'Teeth Whitening', 250.00, NULL, NULL, 11, 16),
(40, b'1', '2025-03-15 10:10:00.000000', 'Dental filling for cavity', 30, 'https://images.pexels.com/photos/3845653/pexels-photo-3845653.jpeg', 'Dental Filling', 120.00, NULL, NULL, 11, 16),
(41, b'1', '2025-02-10 15:00:00.000000', 'Standard oil change service', 30, 'https://images.pexels.com/photos/3806288/pexels-photo-3806288.jpeg', 'Oil Change', 45.00, NULL, NULL, 12, 17),
(42, b'1', '2025-02-10 15:05:00.000000', 'Brake pad replacement', 90, 'https://images.pexels.com/photos/13065690/pexels-photo-13065690.jpeg', 'Brake Service', 180.00, NULL, NULL, 12, 17),
(43, b'1', '2025-02-10 15:10:00.000000', 'Engine diagnostic scan', 45, 'https://images.pexels.com/photos/3806288/pexels-photo-3806288.jpeg', 'Engine Diagnostic', 85.00, NULL, NULL, 12, 17),
(44, b'1', '2025-02-10 15:15:00.000000', 'Tire rotation and balancing', 45, 'https://images.pexels.com/photos/13065690/pexels-photo-13065690.jpeg', 'Tire Service', 65.00, NULL, NULL, 12, 17),
(45, b'1', '2025-01-20 11:00:00.000000', 'Wedding photography full day coverage', 480, 'https://images.pexels.com/photos/1024966/pexels-photo-1024966.jpeg', 'Wedding Photography', 1500.00, NULL, NULL, 13, 18),
(46, b'1', '2025-01-20 11:05:00.000000', 'Family portrait session', 60, 'https://images.pexels.com/photos/1648387/pexels-photo-1648387.jpeg', 'Family Portraits', 200.00, NULL, NULL, 13, 18),
(47, b'1', '2025-01-20 11:10:00.000000', 'Corporate headshots', 30, 'https://images.pexels.com/photos/3760809/pexels-photo-3760809.jpeg', 'Corporate Headshots', 150.00, NULL, NULL, 13, 18),
(48, b'1', '2025-12-01 14:00:00.000000', 'Classic manicure with polish', 45, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', 'Manicure', 30.00, NULL, NULL, 14, 19),
(49, b'1', '2025-12-01 14:05:00.000000', 'Spa pedicure with massage', 60, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', 'Pedicure', 40.00, NULL, NULL, 14, 19),
(50, b'1', '2025-12-01 14:10:00.000000', 'Gel nails with design', 75, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', 'Gel Nails', 55.00, NULL, NULL, 14, 19),
(51, b'1', '2025-12-01 14:15:00.000000', 'Acrylic nail extensions', 90, 'https://images.pexels.com/photos/3997379/pexels-photo-3997379.jpeg', 'Acrylic Nails', 65.00, NULL, NULL, 14, 19),
(52, b'1', '2025-11-15 09:00:00.000000', 'Full dog grooming with bath and haircut', 90, 'https://images.pexels.com/photos/5731890/pexels-photo-5731890.jpeg', 'Dog Grooming', 60.00, NULL, NULL, 15, 20),
(53, b'1', '2025-11-15 09:05:00.000000', 'Cat grooming and styling', 60, 'https://images.pexels.com/photos/7210754/pexels-photo-7210754.jpeg', 'Cat Grooming', 50.00, NULL, NULL, 15, 20),
(54, b'1', '2025-11-15 09:10:00.000000', 'Nail trimming for pets', 20, 'https://images.pexels.com/photos/5731890/pexels-photo-5731890.jpeg', 'Pet Nail Trim', 20.00, NULL, NULL, 15, 20),
(56, b'1', '2026-01-01 21:53:10.000000', 'Quick refresh of fades and edges between full haircuts', 15, 'https://www.artistbarber.com/uploads/stores/304/2ecbab70-46c2-410f-a21d-4168c09576ec_original.jpg', 'Fade Touch-Up', 3.00, NULL, NULL, 1, 2);

-- --------------------------------------------------------

--
-- Table structure for table `service_bookings`
--

CREATE TABLE `service_bookings` (
  `id` bigint(20) NOT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `service_id` bigint(20) NOT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `business_client_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_bookings`
--

INSERT INTO `service_bookings` (`id`, `client_id`, `service_id`, `staff_id`, `business_client_id`) VALUES
(1, 3, 1, 2, NULL),
(2, 16, 1, 2, NULL),
(3, 7, 8, 2, NULL),
(4, 17, 7, 2, NULL),
(5, 18, 1, 5, NULL),
(6, 19, 1, 5, NULL),
(7, 20, 6, 5, NULL),
(8, 21, 1, 26, NULL),
(9, 22, 7, 26, NULL),
(10, 23, 1, 2, NULL),
(11, 24, 8, 2, NULL),
(12, 25, 1, 2, NULL),
(13, 16, 1, 2, NULL),
(14, 17, 7, 5, NULL),
(15, 18, 1, 26, NULL),
(16, 19, 1, 2, NULL),
(17, 20, 1, 2, NULL),
(18, 21, 1, 5, NULL),
(19, 22, 9, 5, NULL),
(20, 3, 1, 2, NULL),
(21, 23, 1, 26, NULL),
(22, 24, 7, 26, NULL),
(23, 25, 1, 2, NULL),
(24, 16, 8, 2, NULL),
(25, 17, 7, 5, NULL),
(26, 18, 1, 2, NULL),
(27, 19, 6, 5, NULL),
(31, NULL, 1, 2, 1),
(32, NULL, 56, 2, 1),
(33, 3, 56, 2, NULL),
(36, 3, 56, 2, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `service_ratings`
--

CREATE TABLE `service_ratings` (
  `id` bigint(20) NOT NULL,
  `client_id` bigint(20) NOT NULL,
  `service_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_ratings`
--

INSERT INTO `service_ratings` (`id`, `client_id`, `service_id`) VALUES
(45, 3, 1);

-- --------------------------------------------------------

--
-- Table structure for table `service_staff`
--

CREATE TABLE `service_staff` (
  `service_id` bigint(20) NOT NULL,
  `staff_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_staff`
--

INSERT INTO `service_staff` (`service_id`, `staff_id`) VALUES
(8, 5),
(6, 5),
(1, 2),
(1, 5),
(6, 5),
(7, 5),
(9, 5),
(1, 26),
(7, 26),
(8, 26),
(10, 16),
(11, 16),
(12, 16),
(10, 30),
(11, 30),
(12, 30),
(13, 17),
(14, 17),
(15, 17),
(16, 18),
(17, 18),
(19, 18),
(16, 27),
(18, 27),
(19, 27),
(20, 19),
(21, 19),
(22, 19),
(20, 28),
(22, 28),
(23, 28),
(24, 20),
(25, 20),
(26, 20),
(27, 21),
(29, 21),
(27, 29),
(28, 29),
(30, 29),
(31, 22),
(32, 22),
(33, 22),
(34, 23),
(35, 23),
(36, 23),
(37, 23),
(38, 24),
(39, 24),
(40, 24),
(41, 25),
(42, 25),
(43, 25),
(44, 25),
(8, 2),
(56, 2);

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

--
-- Dumping data for table `staff`
--

INSERT INTO `staff` (`default_end_time`, `default_start_time`, `start_working_at`, `id`, `business_id`) VALUES
('17:00:00.000000', '08:00:00.000000', '2025-11-27', 2, 1),
('17:30:00.000000', '08:00:00.000000', '2025-11-26', 5, 1),
('22:00:00.000000', '14:00:00.000000', '2024-10-20', 16, 3),
('23:00:00.000000', '15:00:00.000000', '2024-09-25', 17, 4),
('18:00:00.000000', '09:00:00.000000', '2024-11-05', 18, 5),
('18:30:00.000000', '09:00:00.000000', '2024-08-15', 19, 6),
('23:30:00.000000', '15:00:00.000000', '2024-07-10', 20, 7),
('20:00:00.000000', '07:00:00.000000', '2024-06-20', 21, 8),
('23:00:00.000000', '16:00:00.000000', '2024-05-25', 22, 9),
('19:00:00.000000', '10:00:00.000000', '2024-04-28', 23, 10),
('17:00:00.000000', '08:00:00.000000', '2024-03-20', 24, 11),
('17:00:00.000000', '08:00:00.000000', '2024-02-15', 25, 12),
('18:00:00.000000', '09:00:00.000000', '2025-11-26', 26, 1),
('17:30:00.000000', '08:30:00.000000', '2025-11-27', 27, 5),
('18:00:00.000000', '09:00:00.000000', '2025-11-28', 28, 6),
('16:00:00.000000', '07:00:00.000000', '2025-11-29', 29, 8),
('22:00:00.000000', '14:00:00.000000', '2025-11-30', 30, 3);

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
  `status` enum('AVAILABLE','CLOSED','SICK','VACATION','DAY_OFF','UNAVAILABLE') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `staff_id` bigint(20) NOT NULL,
  `user_edited` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `staff_availabilities`
--

INSERT INTO `staff_availabilities` (`id`, `created_at`, `date`, `end_time`, `start_time`, `status`, `updated_at`, `staff_id`, `user_edited`) VALUES
(32, '2025-11-27 00:21:47.000000', '2025-11-27', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(33, '2025-11-27 00:21:47.000000', '2025-11-28', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(34, '2025-11-27 00:21:47.000000', '2025-11-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(35, '2025-11-27 00:21:47.000000', '2025-11-30', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 2, b'0'),
(36, '2025-11-27 00:21:47.000000', '2025-12-01', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 2, b'0'),
(37, '2025-11-27 00:21:47.000000', '2025-12-02', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 2, b'0'),
(38, '2025-11-27 00:21:47.000000', '2025-12-03', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(39, '2025-11-27 00:21:47.000000', '2025-12-04', '17:00:00.000000', '08:00:00.000000', 'SICK', '2025-11-27 00:22:15.000000', 2, b'0'),
(40, '2025-11-27 00:21:47.000000', '2025-12-05', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(41, '2025-11-27 00:21:47.000000', '2025-12-06', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(42, '2025-11-27 00:21:47.000000', '2025-12-07', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 2, b'0'),
(43, '2025-11-27 00:21:47.000000', '2025-12-08', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 2, b'0'),
(44, '2025-11-27 00:21:47.000000', '2025-12-09', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 2, b'0'),
(45, '2025-11-27 00:21:47.000000', '2025-12-10', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(46, '2025-11-27 00:21:47.000000', '2025-12-11', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(47, '2025-11-27 00:21:47.000000', '2025-12-12', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(48, '2025-11-27 00:21:47.000000', '2025-12-13', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(49, '2025-11-27 00:21:47.000000', '2025-12-14', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 2, b'0'),
(50, '2025-11-27 00:21:47.000000', '2025-12-15', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 2, b'0'),
(51, '2025-11-27 00:21:47.000000', '2025-12-16', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 2, b'0'),
(52, '2025-11-27 00:21:47.000000', '2025-12-17', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(53, '2025-11-27 00:21:47.000000', '2025-12-18', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(54, '2025-11-27 00:21:47.000000', '2025-12-19', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(55, '2025-11-27 00:21:47.000000', '2025-12-20', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(56, '2025-11-27 00:21:47.000000', '2025-12-21', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 2, b'0'),
(57, '2025-11-27 00:21:47.000000', '2025-12-22', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 2, b'0'),
(58, '2025-11-27 00:21:47.000000', '2025-12-23', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 2, b'0'),
(59, '2025-11-27 00:21:47.000000', '2025-12-24', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(60, '2025-11-27 00:21:47.000000', '2025-12-25', '16:00:00.392000', '10:00:00.344000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(61, '2025-11-27 00:21:47.000000', '2025-12-26', '15:00:00.872000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(62, '2025-11-27 00:21:47.000000', '2025-12-27', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 2, b'0'),
(63, '2025-11-27 00:21:47.000000', '2025-11-27', '17:30:00.000000', '08:00:00.000000', 'VACATION', '2025-11-27 00:22:15.000000', 5, b'0'),
(64, '2025-11-27 00:21:47.000000', '2025-11-28', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(65, '2025-11-27 00:21:47.000000', '2025-11-29', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(66, '2025-11-27 00:21:47.000000', '2025-11-30', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 5, b'0'),
(67, '2025-11-27 00:21:47.000000', '2025-12-01', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 5, b'0'),
(68, '2025-11-27 00:21:47.000000', '2025-12-02', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 5, b'0'),
(69, '2025-11-27 00:21:47.000000', '2025-12-03', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(70, '2025-11-27 00:21:47.000000', '2025-12-04', '17:30:00.000000', '08:00:00.000000', 'DAY_OFF', '2025-11-27 23:24:07.000000', 5, b'1'),
(71, '2025-11-27 00:21:47.000000', '2025-12-05', '17:30:00.000000', '08:00:00.000000', 'VACATION', '2025-11-27 00:22:15.000000', 5, b'0'),
(72, '2025-11-27 00:21:47.000000', '2025-12-06', '17:30:00.000000', '08:00:00.000000', 'UNAVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(73, '2025-11-27 00:21:47.000000', '2025-12-07', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 5, b'0'),
(74, '2025-11-27 00:21:47.000000', '2025-12-08', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 5, b'0'),
(75, '2025-11-27 00:21:47.000000', '2025-12-09', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 5, b'0'),
(76, '2025-11-27 00:21:47.000000', '2025-12-10', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(77, '2025-11-27 00:21:47.000000', '2025-12-11', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(78, '2025-11-27 00:21:47.000000', '2025-12-12', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(79, '2025-11-27 00:21:47.000000', '2025-12-13', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(80, '2025-11-27 00:21:47.000000', '2025-12-14', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 5, b'0'),
(81, '2025-11-27 00:21:47.000000', '2025-12-15', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 5, b'0'),
(82, '2025-11-27 00:21:47.000000', '2025-12-16', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 5, b'0'),
(83, '2025-11-27 00:21:47.000000', '2025-12-17', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(84, '2025-11-27 00:21:47.000000', '2025-12-18', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(85, '2025-11-27 00:21:47.000000', '2025-12-19', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(86, '2025-11-27 00:21:47.000000', '2025-12-20', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(87, '2025-11-27 00:21:47.000000', '2025-12-21', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 00:22:15.000000', 5, b'0'),
(88, '2025-11-27 00:21:47.000000', '2025-12-22', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 02:15:53.000000', 5, b'0'),
(89, '2025-11-27 00:21:47.000000', '2025-12-23', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-11-27 02:15:53.000000', 5, b'0'),
(90, '2025-11-27 00:21:47.000000', '2025-12-24', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(91, '2025-11-27 00:21:47.000000', '2025-12-25', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(92, '2025-11-27 00:21:48.000000', '2025-12-26', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(93, '2025-11-27 00:21:48.000000', '2025-12-27', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-11-27 00:22:15.000000', 5, b'0'),
(94, '2025-11-28 01:00:00.000000', '2025-12-28', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 01:30:27.000000', 2, b'0'),
(95, '2025-11-28 01:00:00.000000', '2025-12-28', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 01:30:27.000000', 5, b'0'),
(96, '2025-12-24 01:30:27.000000', '2025-12-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:42.000000', 2, b'0'),
(97, '2025-12-24 01:30:27.000000', '2025-12-30', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:42.000000', 2, b'0'),
(98, '2025-12-24 01:30:27.000000', '2025-12-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:42.000000', 2, b'0'),
(99, '2025-12-24 01:30:27.000000', '2026-01-01', '18:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-01 16:35:10.000000', 2, b'1'),
(100, '2025-12-24 01:30:27.000000', '2026-01-02', '17:00:00.000000', '09:00:00.361000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(101, '2025-12-24 01:30:27.000000', '2026-01-03', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(102, '2025-12-24 01:30:27.000000', '2026-01-04', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 2, b'0'),
(103, '2025-12-24 01:30:27.000000', '2026-01-05', '17:00:00.000000', '08:00:00.000000', 'SICK', '2025-12-24 18:06:43.000000', 2, b'0'),
(104, '2025-12-24 01:30:27.000000', '2026-01-06', '15:00:00.000000', '10:00:00.000000', 'VACATION', '2026-01-02 23:38:54.000000', 2, b'1'),
(105, '2025-12-24 01:30:27.000000', '2026-01-07', '17:00:00.000000', '08:00:00.000000', 'VACATION', '2026-01-02 23:38:54.000000', 2, b'1'),
(106, '2025-12-24 01:30:27.000000', '2026-01-08', '17:00:00.000000', '08:00:00.000000', 'VACATION', '2026-01-02 23:38:54.000000', 2, b'1'),
(107, '2025-12-24 01:30:27.000000', '2026-01-09', '17:00:00.000000', '08:00:00.000000', 'DAY_OFF', '2025-12-24 18:06:43.000000', 2, b'0'),
(108, '2025-12-24 01:30:27.000000', '2026-01-10', '17:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2026-01-01 16:39:34.000000', 2, b'1'),
(109, '2025-12-24 01:30:27.000000', '2026-01-11', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 2, b'0'),
(110, '2025-12-24 01:30:27.000000', '2026-01-12', '17:00:00.000000', '08:00:00.000000', 'VACATION', '2025-12-24 18:06:43.000000', 2, b'0'),
(111, '2025-12-24 01:30:27.000000', '2026-01-13', '15:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2026-01-01 16:46:41.000000', 2, b'1'),
(112, '2025-12-24 01:30:27.000000', '2026-01-14', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(113, '2025-12-24 01:30:27.000000', '2026-01-15', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(114, '2025-12-24 01:30:27.000000', '2026-01-16', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(115, '2025-12-24 01:30:27.000000', '2026-01-17', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(116, '2025-12-24 01:30:27.000000', '2026-01-18', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 2, b'0'),
(117, '2025-12-24 01:30:27.000000', '2026-01-19', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(118, '2025-12-24 01:30:27.000000', '2026-01-20', '17:00:00.000000', '08:00:00.000000', 'DAY_OFF', '2025-12-31 18:42:46.000000', 2, b'1'),
(119, '2025-12-24 01:30:27.000000', '2026-01-21', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(120, '2025-12-24 01:30:27.000000', '2026-01-22', '17:00:00.000000', '08:00:00.000000', 'UNAVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(121, '2025-12-24 01:30:27.000000', '2026-01-23', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(122, '2025-12-24 01:30:27.000000', '2026-01-24', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 2, b'0'),
(123, '2025-12-24 01:30:27.000000', '2025-12-29', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(124, '2025-12-24 01:30:27.000000', '2025-12-30', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(125, '2025-12-24 01:30:27.000000', '2025-12-31', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(126, '2025-12-24 01:30:27.000000', '2026-01-01', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(127, '2025-12-24 01:30:27.000000', '2026-01-02', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(128, '2025-12-24 01:30:27.000000', '2026-01-03', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(129, '2025-12-24 01:30:27.000000', '2026-01-04', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(130, '2025-12-24 01:30:27.000000', '2026-01-05', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(131, '2025-12-24 01:30:27.000000', '2026-01-06', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(132, '2025-12-24 01:30:27.000000', '2026-01-07', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(133, '2025-12-24 01:30:27.000000', '2026-01-08', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(134, '2025-12-24 01:30:27.000000', '2026-01-09', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(135, '2025-12-24 01:30:27.000000', '2026-01-10', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(136, '2025-12-24 01:30:27.000000', '2026-01-11', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(137, '2025-12-24 01:30:27.000000', '2026-01-12', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(138, '2025-12-24 01:30:27.000000', '2026-01-13', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(139, '2025-12-24 01:30:27.000000', '2026-01-14', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(140, '2025-12-24 01:30:27.000000', '2026-01-15', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(141, '2025-12-24 01:30:27.000000', '2026-01-16', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(142, '2025-12-24 01:30:27.000000', '2026-01-17', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(143, '2025-12-24 01:30:27.000000', '2026-01-18', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(144, '2025-12-24 01:30:27.000000', '2026-01-19', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(145, '2025-12-24 01:30:27.000000', '2026-01-20', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-24 18:06:43.000000', 5, b'0'),
(146, '2025-12-24 01:30:27.000000', '2026-01-21', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(147, '2025-12-24 01:30:27.000000', '2026-01-22', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(148, '2025-12-24 01:30:27.000000', '2026-01-23', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(149, '2025-12-24 01:30:27.000000', '2026-01-24', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:43.000000', 5, b'0'),
(150, '2025-12-24 18:06:43.000000', '2025-12-24', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 26, b'0'),
(151, '2025-12-24 18:06:43.000000', '2025-12-25', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 26, b'0'),
(152, '2025-12-24 18:06:43.000000', '2025-12-26', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(153, '2025-12-24 18:06:43.000000', '2025-12-27', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(154, '2025-12-24 18:06:43.000000', '2025-12-28', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(155, '2025-12-24 18:06:43.000000', '2025-12-29', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(156, '2025-12-24 18:06:43.000000', '2025-12-30', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(157, '2025-12-24 18:06:43.000000', '2025-12-31', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(158, '2025-12-24 18:06:43.000000', '2026-01-01', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(159, '2025-12-24 18:06:43.000000', '2026-01-02', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(160, '2025-12-24 18:06:43.000000', '2026-01-03', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(161, '2025-12-24 18:06:43.000000', '2026-01-04', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(162, '2025-12-24 18:06:43.000000', '2026-01-05', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(163, '2025-12-24 18:06:43.000000', '2026-01-06', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(164, '2025-12-24 18:06:43.000000', '2026-01-07', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(165, '2025-12-24 18:06:43.000000', '2026-01-08', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(166, '2025-12-24 18:06:43.000000', '2026-01-09', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(167, '2025-12-24 18:06:43.000000', '2026-01-10', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(168, '2025-12-24 18:06:43.000000', '2026-01-11', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(169, '2025-12-24 18:06:43.000000', '2026-01-12', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(170, '2025-12-24 18:06:43.000000', '2026-01-13', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(171, '2025-12-24 18:06:43.000000', '2026-01-14', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(172, '2025-12-24 18:06:43.000000', '2026-01-15', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(173, '2025-12-24 18:06:43.000000', '2026-01-16', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(174, '2025-12-24 18:06:43.000000', '2026-01-17', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(175, '2025-12-24 18:06:43.000000', '2026-01-18', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(176, '2025-12-24 18:06:43.000000', '2026-01-19', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(177, '2025-12-24 18:06:43.000000', '2026-01-20', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 26, b'0'),
(178, '2025-12-24 18:06:43.000000', '2026-01-21', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(179, '2025-12-24 18:06:43.000000', '2026-01-22', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(180, '2025-12-24 18:06:43.000000', '2026-01-23', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(181, '2025-12-24 18:06:43.000000', '2026-01-24', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 26, b'0'),
(182, '2025-12-24 18:06:43.000000', '2025-12-24', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 16, b'0'),
(183, '2025-12-24 18:06:43.000000', '2025-12-25', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 16, b'0'),
(184, '2025-12-24 18:06:43.000000', '2025-12-26', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(185, '2025-12-24 18:06:43.000000', '2025-12-27', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(186, '2025-12-24 18:06:43.000000', '2025-12-28', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(187, '2025-12-24 18:06:43.000000', '2025-12-29', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(188, '2025-12-24 18:06:43.000000', '2025-12-30', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(189, '2025-12-24 18:06:43.000000', '2025-12-31', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(190, '2025-12-24 18:06:43.000000', '2026-01-01', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(191, '2025-12-24 18:06:43.000000', '2026-01-02', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(192, '2025-12-24 18:06:43.000000', '2026-01-03', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(193, '2025-12-24 18:06:43.000000', '2026-01-04', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(194, '2025-12-24 18:06:43.000000', '2026-01-05', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(195, '2025-12-24 18:06:43.000000', '2026-01-06', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(196, '2025-12-24 18:06:43.000000', '2026-01-07', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(197, '2025-12-24 18:06:43.000000', '2026-01-08', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(198, '2025-12-24 18:06:43.000000', '2026-01-09', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(199, '2025-12-24 18:06:43.000000', '2026-01-10', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(200, '2025-12-24 18:06:43.000000', '2026-01-11', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(201, '2025-12-24 18:06:43.000000', '2026-01-12', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(202, '2025-12-24 18:06:43.000000', '2026-01-13', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(203, '2025-12-24 18:06:43.000000', '2026-01-14', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(204, '2025-12-24 18:06:43.000000', '2026-01-15', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(205, '2025-12-24 18:06:43.000000', '2026-01-16', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(206, '2025-12-24 18:06:43.000000', '2026-01-17', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(207, '2025-12-24 18:06:43.000000', '2026-01-18', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(208, '2025-12-24 18:06:43.000000', '2026-01-19', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 16, b'0'),
(209, '2025-12-24 18:06:43.000000', '2026-01-20', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(210, '2025-12-24 18:06:43.000000', '2026-01-21', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(211, '2025-12-24 18:06:43.000000', '2026-01-22', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(212, '2025-12-24 18:06:43.000000', '2026-01-23', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(213, '2025-12-24 18:06:43.000000', '2026-01-24', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 16, b'0'),
(214, '2025-12-24 18:06:43.000000', '2025-12-24', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 30, b'0'),
(215, '2025-12-24 18:06:43.000000', '2025-12-25', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 30, b'0'),
(216, '2025-12-24 18:06:43.000000', '2025-12-26', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 30, b'0'),
(217, '2025-12-24 18:06:43.000000', '2025-12-27', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 30, b'0'),
(218, '2025-12-24 18:06:43.000000', '2025-12-28', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 30, b'0'),
(219, '2025-12-24 18:06:43.000000', '2025-12-29', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:00.000000', 30, b'0'),
(220, '2025-12-24 18:06:43.000000', '2025-12-30', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:00.000000', 30, b'0'),
(221, '2025-12-24 18:06:43.000000', '2025-12-31', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(222, '2025-12-24 18:06:43.000000', '2026-01-01', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(223, '2025-12-24 18:06:43.000000', '2026-01-02', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(224, '2025-12-24 18:06:43.000000', '2026-01-03', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(225, '2025-12-24 18:06:43.000000', '2026-01-04', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 30, b'0'),
(226, '2025-12-24 18:06:43.000000', '2026-01-05', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 30, b'0'),
(227, '2025-12-24 18:06:43.000000', '2026-01-06', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(228, '2025-12-24 18:06:43.000000', '2026-01-07', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(229, '2025-12-24 18:06:43.000000', '2026-01-08', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(230, '2025-12-24 18:06:43.000000', '2026-01-09', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(231, '2025-12-24 18:06:43.000000', '2026-01-10', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(232, '2025-12-24 18:06:43.000000', '2026-01-11', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 30, b'0'),
(233, '2025-12-24 18:06:43.000000', '2026-01-12', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 30, b'0'),
(234, '2025-12-24 18:06:43.000000', '2026-01-13', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(235, '2025-12-24 18:06:43.000000', '2026-01-14', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(236, '2025-12-24 18:06:43.000000', '2026-01-15', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(237, '2025-12-24 18:06:43.000000', '2026-01-16', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(238, '2025-12-24 18:06:43.000000', '2026-01-17', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(239, '2025-12-24 18:06:43.000000', '2026-01-18', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 30, b'0'),
(240, '2025-12-24 18:06:43.000000', '2026-01-19', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 30, b'0'),
(241, '2025-12-24 18:06:43.000000', '2026-01-20', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(242, '2025-12-24 18:06:43.000000', '2026-01-21', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(243, '2025-12-24 18:06:43.000000', '2026-01-22', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(244, '2025-12-24 18:06:43.000000', '2026-01-23', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(245, '2025-12-24 18:06:43.000000', '2026-01-24', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 30, b'0'),
(246, '2025-12-24 18:06:43.000000', '2025-12-24', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(247, '2025-12-24 18:06:43.000000', '2025-12-25', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(248, '2025-12-24 18:06:43.000000', '2025-12-26', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(249, '2025-12-24 18:06:43.000000', '2025-12-27', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(250, '2025-12-24 18:06:43.000000', '2025-12-28', '23:00:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 17, b'0'),
(251, '2025-12-24 18:06:43.000000', '2025-12-29', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(252, '2025-12-24 18:06:43.000000', '2025-12-30', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(253, '2025-12-24 18:06:43.000000', '2025-12-31', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(254, '2025-12-24 18:06:43.000000', '2026-01-01', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(255, '2025-12-24 18:06:43.000000', '2026-01-02', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(256, '2025-12-24 18:06:43.000000', '2026-01-03', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(257, '2025-12-24 18:06:43.000000', '2026-01-04', '23:00:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 17, b'0'),
(258, '2025-12-24 18:06:43.000000', '2026-01-05', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(259, '2025-12-24 18:06:43.000000', '2026-01-06', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(260, '2025-12-24 18:06:43.000000', '2026-01-07', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(261, '2025-12-24 18:06:43.000000', '2026-01-08', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(262, '2025-12-24 18:06:43.000000', '2026-01-09', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(263, '2025-12-24 18:06:43.000000', '2026-01-10', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(264, '2025-12-24 18:06:43.000000', '2026-01-11', '23:00:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 17, b'0'),
(265, '2025-12-24 18:06:43.000000', '2026-01-12', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(266, '2025-12-24 18:06:43.000000', '2026-01-13', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(267, '2025-12-24 18:06:43.000000', '2026-01-14', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(268, '2025-12-24 18:06:43.000000', '2026-01-15', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(269, '2025-12-24 18:06:43.000000', '2026-01-16', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(270, '2025-12-24 18:06:43.000000', '2026-01-17', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(271, '2025-12-24 18:06:43.000000', '2026-01-18', '23:00:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 17, b'0'),
(272, '2025-12-24 18:06:43.000000', '2026-01-19', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(273, '2025-12-24 18:06:43.000000', '2026-01-20', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(274, '2025-12-24 18:06:43.000000', '2026-01-21', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(275, '2025-12-24 18:06:43.000000', '2026-01-22', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(276, '2025-12-24 18:06:43.000000', '2026-01-23', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(277, '2025-12-24 18:06:43.000000', '2026-01-24', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 17, b'0'),
(278, '2025-12-24 18:06:43.000000', '2025-12-24', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(279, '2025-12-24 18:06:43.000000', '2025-12-25', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(280, '2025-12-24 18:06:43.000000', '2025-12-26', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(281, '2025-12-24 18:06:43.000000', '2025-12-27', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(282, '2025-12-24 18:06:43.000000', '2025-12-28', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 18, b'0'),
(283, '2025-12-24 18:06:43.000000', '2025-12-29', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(284, '2025-12-24 18:06:43.000000', '2025-12-30', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(285, '2025-12-24 18:06:43.000000', '2025-12-31', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(286, '2025-12-24 18:06:43.000000', '2026-01-01', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(287, '2025-12-24 18:06:43.000000', '2026-01-02', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(288, '2025-12-24 18:06:43.000000', '2026-01-03', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(289, '2025-12-24 18:06:43.000000', '2026-01-04', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 18, b'0'),
(290, '2025-12-24 18:06:43.000000', '2026-01-05', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(291, '2025-12-24 18:06:43.000000', '2026-01-06', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(292, '2025-12-24 18:06:43.000000', '2026-01-07', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(293, '2025-12-24 18:06:43.000000', '2026-01-08', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(294, '2025-12-24 18:06:43.000000', '2026-01-09', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(295, '2025-12-24 18:06:43.000000', '2026-01-10', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(296, '2025-12-24 18:06:43.000000', '2026-01-11', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 18, b'0'),
(297, '2025-12-24 18:06:43.000000', '2026-01-12', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(298, '2025-12-24 18:06:43.000000', '2026-01-13', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(299, '2025-12-24 18:06:43.000000', '2026-01-14', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(300, '2025-12-24 18:06:43.000000', '2026-01-15', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(301, '2025-12-24 18:06:43.000000', '2026-01-16', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(302, '2025-12-24 18:06:43.000000', '2026-01-17', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(303, '2025-12-24 18:06:43.000000', '2026-01-18', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 18, b'0'),
(304, '2025-12-24 18:06:43.000000', '2026-01-19', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(305, '2025-12-24 18:06:43.000000', '2026-01-20', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(306, '2025-12-24 18:06:43.000000', '2026-01-21', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(307, '2025-12-24 18:06:43.000000', '2026-01-22', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(308, '2025-12-24 18:06:43.000000', '2026-01-23', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(309, '2025-12-24 18:06:43.000000', '2026-01-24', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 18, b'0'),
(310, '2025-12-24 18:06:43.000000', '2025-12-24', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0'),
(311, '2025-12-24 18:06:43.000000', '2025-12-25', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0'),
(312, '2025-12-24 18:06:43.000000', '2025-12-26', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(313, '2025-12-24 18:06:43.000000', '2025-12-27', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(314, '2025-12-24 18:06:43.000000', '2025-12-28', '17:30:00.000000', '08:30:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 27, b'0'),
(315, '2025-12-24 18:06:43.000000', '2025-12-29', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(316, '2025-12-24 18:06:43.000000', '2025-12-30', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(317, '2025-12-24 18:06:43.000000', '2025-12-31', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(318, '2025-12-24 18:06:43.000000', '2026-01-01', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(319, '2025-12-24 18:06:43.000000', '2026-01-02', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(320, '2025-12-24 18:06:43.000000', '2026-01-03', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(321, '2025-12-24 18:06:43.000000', '2026-01-04', '17:30:00.000000', '08:30:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 27, b'0'),
(322, '2025-12-24 18:06:43.000000', '2026-01-05', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(323, '2025-12-24 18:06:43.000000', '2026-01-06', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(324, '2025-12-24 18:06:43.000000', '2026-01-07', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(325, '2025-12-24 18:06:43.000000', '2026-01-08', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(326, '2025-12-24 18:06:43.000000', '2026-01-09', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(327, '2025-12-24 18:06:43.000000', '2026-01-10', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(328, '2025-12-24 18:06:43.000000', '2026-01-11', '17:30:00.000000', '08:30:00.000000', 'CLOSED', '2025-12-26 01:00:01.000000', 27, b'0'),
(329, '2025-12-24 18:06:43.000000', '2026-01-12', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(330, '2025-12-24 18:06:43.000000', '2026-01-13', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(331, '2025-12-24 18:06:43.000000', '2026-01-14', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(332, '2025-12-24 18:06:43.000000', '2026-01-15', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:01.000000', 27, b'0'),
(333, '2025-12-24 18:06:43.000000', '2026-01-16', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(334, '2025-12-24 18:06:43.000000', '2026-01-17', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(335, '2025-12-24 18:06:43.000000', '2026-01-18', '17:30:00.000000', '08:30:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 27, b'0'),
(336, '2025-12-24 18:06:43.000000', '2026-01-19', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(337, '2025-12-24 18:06:43.000000', '2026-01-20', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(338, '2025-12-24 18:06:43.000000', '2026-01-21', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(339, '2025-12-24 18:06:43.000000', '2026-01-22', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(340, '2025-12-24 18:06:43.000000', '2026-01-23', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(341, '2025-12-24 18:06:43.000000', '2026-01-24', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 27, b'0'),
(342, '2025-12-24 18:06:43.000000', '2025-12-24', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 19, b'0'),
(343, '2025-12-24 18:06:43.000000', '2025-12-25', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 19, b'0'),
(344, '2025-12-24 18:06:43.000000', '2025-12-26', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(345, '2025-12-24 18:06:43.000000', '2025-12-27', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(346, '2025-12-24 18:06:43.000000', '2025-12-28', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(347, '2025-12-24 18:06:43.000000', '2025-12-29', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(348, '2025-12-24 18:06:43.000000', '2025-12-30', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(349, '2025-12-24 18:06:43.000000', '2025-12-31', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(350, '2025-12-24 18:06:43.000000', '2026-01-01', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(351, '2025-12-24 18:06:43.000000', '2026-01-02', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(352, '2025-12-24 18:06:43.000000', '2026-01-03', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(353, '2025-12-24 18:06:43.000000', '2026-01-04', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(354, '2025-12-24 18:06:43.000000', '2026-01-05', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(355, '2025-12-24 18:06:43.000000', '2026-01-06', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(356, '2025-12-24 18:06:43.000000', '2026-01-07', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(357, '2025-12-24 18:06:43.000000', '2026-01-08', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(358, '2025-12-24 18:06:43.000000', '2026-01-09', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(359, '2025-12-24 18:06:43.000000', '2026-01-10', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(360, '2025-12-24 18:06:43.000000', '2026-01-11', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(361, '2025-12-24 18:06:43.000000', '2026-01-12', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(362, '2025-12-24 18:06:43.000000', '2026-01-13', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(363, '2025-12-24 18:06:43.000000', '2026-01-14', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(364, '2025-12-24 18:06:43.000000', '2026-01-15', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(365, '2025-12-24 18:06:43.000000', '2026-01-16', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(366, '2025-12-24 18:06:43.000000', '2026-01-17', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(367, '2025-12-24 18:06:43.000000', '2026-01-18', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(368, '2025-12-24 18:06:43.000000', '2026-01-19', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 19, b'0'),
(369, '2025-12-24 18:06:43.000000', '2026-01-20', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(370, '2025-12-24 18:06:43.000000', '2026-01-21', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(371, '2025-12-24 18:06:43.000000', '2026-01-22', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(372, '2025-12-24 18:06:43.000000', '2026-01-23', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(373, '2025-12-24 18:06:43.000000', '2026-01-24', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 19, b'0'),
(374, '2025-12-24 18:06:43.000000', '2025-12-24', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 28, b'0'),
(375, '2025-12-24 18:06:43.000000', '2025-12-25', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 28, b'0'),
(376, '2025-12-24 18:06:43.000000', '2025-12-26', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(377, '2025-12-24 18:06:43.000000', '2025-12-27', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(378, '2025-12-24 18:06:43.000000', '2025-12-28', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(379, '2025-12-24 18:06:43.000000', '2025-12-29', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(380, '2025-12-24 18:06:43.000000', '2025-12-30', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(381, '2025-12-24 18:06:43.000000', '2025-12-31', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(382, '2025-12-24 18:06:43.000000', '2026-01-01', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(383, '2025-12-24 18:06:43.000000', '2026-01-02', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(384, '2025-12-24 18:06:43.000000', '2026-01-03', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(385, '2025-12-24 18:06:43.000000', '2026-01-04', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(386, '2025-12-24 18:06:43.000000', '2026-01-05', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(387, '2025-12-24 18:06:43.000000', '2026-01-06', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(388, '2025-12-24 18:06:43.000000', '2026-01-07', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(389, '2025-12-24 18:06:43.000000', '2026-01-08', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(390, '2025-12-24 18:06:43.000000', '2026-01-09', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(391, '2025-12-24 18:06:43.000000', '2026-01-10', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(392, '2025-12-24 18:06:43.000000', '2026-01-11', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0');
INSERT INTO `staff_availabilities` (`id`, `created_at`, `date`, `end_time`, `start_time`, `status`, `updated_at`, `staff_id`, `user_edited`) VALUES
(393, '2025-12-24 18:06:43.000000', '2026-01-12', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(394, '2025-12-24 18:06:43.000000', '2026-01-13', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(395, '2025-12-24 18:06:43.000000', '2026-01-14', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(396, '2025-12-24 18:06:43.000000', '2026-01-15', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(397, '2025-12-24 18:06:43.000000', '2026-01-16', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(398, '2025-12-24 18:06:43.000000', '2026-01-17', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(399, '2025-12-24 18:06:43.000000', '2026-01-18', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(400, '2025-12-24 18:06:43.000000', '2026-01-19', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 28, b'0'),
(401, '2025-12-24 18:06:43.000000', '2026-01-20', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(402, '2025-12-24 18:06:43.000000', '2026-01-21', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(403, '2025-12-24 18:06:43.000000', '2026-01-22', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(404, '2025-12-24 18:06:43.000000', '2026-01-23', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(405, '2025-12-24 18:06:43.000000', '2026-01-24', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 28, b'0'),
(406, '2025-12-24 18:06:43.000000', '2025-12-24', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 20, b'0'),
(407, '2025-12-24 18:06:43.000000', '2025-12-25', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 20, b'0'),
(408, '2025-12-24 18:06:43.000000', '2025-12-26', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(409, '2025-12-24 18:06:43.000000', '2025-12-27', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(410, '2025-12-24 18:06:43.000000', '2025-12-28', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(411, '2025-12-24 18:06:43.000000', '2025-12-29', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(412, '2025-12-24 18:06:43.000000', '2025-12-30', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(413, '2025-12-24 18:06:43.000000', '2025-12-31', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(414, '2025-12-24 18:06:43.000000', '2026-01-01', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(415, '2025-12-24 18:06:43.000000', '2026-01-02', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(416, '2025-12-24 18:06:43.000000', '2026-01-03', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(417, '2025-12-24 18:06:43.000000', '2026-01-04', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(418, '2025-12-24 18:06:43.000000', '2026-01-05', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(419, '2025-12-24 18:06:43.000000', '2026-01-06', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(420, '2025-12-24 18:06:43.000000', '2026-01-07', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(421, '2025-12-24 18:06:43.000000', '2026-01-08', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(422, '2025-12-24 18:06:43.000000', '2026-01-09', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(423, '2025-12-24 18:06:43.000000', '2026-01-10', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(424, '2025-12-24 18:06:43.000000', '2026-01-11', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(425, '2025-12-24 18:06:43.000000', '2026-01-12', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(426, '2025-12-24 18:06:43.000000', '2026-01-13', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(427, '2025-12-24 18:06:43.000000', '2026-01-14', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(428, '2025-12-24 18:06:43.000000', '2026-01-15', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(429, '2025-12-24 18:06:43.000000', '2026-01-16', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(430, '2025-12-24 18:06:43.000000', '2026-01-17', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(431, '2025-12-24 18:06:43.000000', '2026-01-18', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(432, '2025-12-24 18:06:43.000000', '2026-01-19', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(433, '2025-12-24 18:06:43.000000', '2026-01-20', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 20, b'0'),
(434, '2025-12-24 18:06:43.000000', '2026-01-21', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(435, '2025-12-24 18:06:43.000000', '2026-01-22', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(436, '2025-12-24 18:06:43.000000', '2026-01-23', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(437, '2025-12-24 18:06:43.000000', '2026-01-24', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 20, b'0'),
(438, '2025-12-24 18:06:43.000000', '2025-12-24', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 21, b'0'),
(439, '2025-12-24 18:06:43.000000', '2025-12-25', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 21, b'0'),
(440, '2025-12-24 18:06:43.000000', '2025-12-26', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(441, '2025-12-24 18:06:43.000000', '2025-12-27', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(442, '2025-12-24 18:06:43.000000', '2025-12-28', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(443, '2025-12-24 18:06:43.000000', '2025-12-29', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(444, '2025-12-24 18:06:43.000000', '2025-12-30', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(445, '2025-12-24 18:06:43.000000', '2025-12-31', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(446, '2025-12-24 18:06:43.000000', '2026-01-01', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(447, '2025-12-24 18:06:43.000000', '2026-01-02', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(448, '2025-12-24 18:06:43.000000', '2026-01-03', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(449, '2025-12-24 18:06:43.000000', '2026-01-04', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(450, '2025-12-24 18:06:43.000000', '2026-01-05', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(451, '2025-12-24 18:06:43.000000', '2026-01-06', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(452, '2025-12-24 18:06:43.000000', '2026-01-07', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(453, '2025-12-24 18:06:43.000000', '2026-01-08', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(454, '2025-12-24 18:06:43.000000', '2026-01-09', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(455, '2025-12-24 18:06:43.000000', '2026-01-10', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(456, '2025-12-24 18:06:43.000000', '2026-01-11', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(457, '2025-12-24 18:06:43.000000', '2026-01-12', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(458, '2025-12-24 18:06:43.000000', '2026-01-13', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(459, '2025-12-24 18:06:43.000000', '2026-01-14', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(460, '2025-12-24 18:06:43.000000', '2026-01-15', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(461, '2025-12-24 18:06:43.000000', '2026-01-16', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(462, '2025-12-24 18:06:43.000000', '2026-01-17', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(463, '2025-12-24 18:06:43.000000', '2026-01-18', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(464, '2025-12-24 18:06:43.000000', '2026-01-19', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(465, '2025-12-24 18:06:43.000000', '2026-01-20', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(466, '2025-12-24 18:06:43.000000', '2026-01-21', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(467, '2025-12-24 18:06:43.000000', '2026-01-22', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(468, '2025-12-24 18:06:43.000000', '2026-01-23', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 21, b'0'),
(469, '2025-12-24 18:06:43.000000', '2026-01-24', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 21, b'0'),
(470, '2025-12-24 18:06:43.000000', '2025-12-24', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 29, b'0'),
(471, '2025-12-24 18:06:43.000000', '2025-12-25', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 29, b'0'),
(472, '2025-12-24 18:06:43.000000', '2025-12-26', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(473, '2025-12-24 18:06:43.000000', '2025-12-27', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(474, '2025-12-24 18:06:43.000000', '2025-12-28', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(475, '2025-12-24 18:06:43.000000', '2025-12-29', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(476, '2025-12-24 18:06:43.000000', '2025-12-30', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(477, '2025-12-24 18:06:43.000000', '2025-12-31', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(478, '2025-12-24 18:06:43.000000', '2026-01-01', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(479, '2025-12-24 18:06:43.000000', '2026-01-02', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(480, '2025-12-24 18:06:43.000000', '2026-01-03', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(481, '2025-12-24 18:06:43.000000', '2026-01-04', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(482, '2025-12-24 18:06:43.000000', '2026-01-05', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(483, '2025-12-24 18:06:43.000000', '2026-01-06', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(484, '2025-12-24 18:06:43.000000', '2026-01-07', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(485, '2025-12-24 18:06:43.000000', '2026-01-08', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(486, '2025-12-24 18:06:43.000000', '2026-01-09', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(487, '2025-12-24 18:06:43.000000', '2026-01-10', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(488, '2025-12-24 18:06:43.000000', '2026-01-11', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(489, '2025-12-24 18:06:43.000000', '2026-01-12', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(490, '2025-12-24 18:06:43.000000', '2026-01-13', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(491, '2025-12-24 18:06:43.000000', '2026-01-14', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(492, '2025-12-24 18:06:43.000000', '2026-01-15', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(493, '2025-12-24 18:06:43.000000', '2026-01-16', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(494, '2025-12-24 18:06:43.000000', '2026-01-17', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(495, '2025-12-24 18:06:43.000000', '2026-01-18', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(496, '2025-12-24 18:06:43.000000', '2026-01-19', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(497, '2025-12-24 18:06:43.000000', '2026-01-20', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(498, '2025-12-24 18:06:43.000000', '2026-01-21', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(499, '2025-12-24 18:06:43.000000', '2026-01-22', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(500, '2025-12-24 18:06:43.000000', '2026-01-23', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 29, b'0'),
(501, '2025-12-24 18:06:43.000000', '2026-01-24', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 29, b'0'),
(502, '2025-12-24 18:06:43.000000', '2025-12-24', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', NULL, 22, b'0'),
(503, '2025-12-24 18:06:43.000000', '2025-12-25', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', NULL, 22, b'0'),
(504, '2025-12-24 18:06:43.000000', '2025-12-26', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(505, '2025-12-24 18:06:43.000000', '2025-12-27', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(506, '2025-12-24 18:06:43.000000', '2025-12-28', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 22, b'0'),
(507, '2025-12-24 18:06:43.000000', '2025-12-29', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 22, b'0'),
(508, '2025-12-24 18:06:43.000000', '2025-12-30', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(509, '2025-12-24 18:06:43.000000', '2025-12-31', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(510, '2025-12-24 18:06:43.000000', '2026-01-01', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(511, '2025-12-24 18:06:43.000000', '2026-01-02', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(512, '2025-12-24 18:06:43.000000', '2026-01-03', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(513, '2025-12-24 18:06:43.000000', '2026-01-04', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 22, b'0'),
(514, '2025-12-24 18:06:43.000000', '2026-01-05', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:02.000000', 22, b'0'),
(515, '2025-12-24 18:06:43.000000', '2026-01-06', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:02.000000', 22, b'0'),
(516, '2025-12-24 18:06:43.000000', '2026-01-07', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(517, '2025-12-24 18:06:43.000000', '2026-01-08', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(518, '2025-12-24 18:06:43.000000', '2026-01-09', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(519, '2025-12-24 18:06:43.000000', '2026-01-10', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(520, '2025-12-24 18:06:43.000000', '2026-01-11', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 22, b'0'),
(521, '2025-12-24 18:06:43.000000', '2026-01-12', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 22, b'0'),
(522, '2025-12-24 18:06:43.000000', '2026-01-13', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(523, '2025-12-24 18:06:43.000000', '2026-01-14', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(524, '2025-12-24 18:06:43.000000', '2026-01-15', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(525, '2025-12-24 18:06:43.000000', '2026-01-16', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(526, '2025-12-24 18:06:43.000000', '2026-01-17', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(527, '2025-12-24 18:06:43.000000', '2026-01-18', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 22, b'0'),
(528, '2025-12-24 18:06:43.000000', '2026-01-19', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 22, b'0'),
(529, '2025-12-24 18:06:43.000000', '2026-01-20', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(530, '2025-12-24 18:06:43.000000', '2026-01-21', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(531, '2025-12-24 18:06:43.000000', '2026-01-22', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(532, '2025-12-24 18:06:43.000000', '2026-01-23', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(533, '2025-12-24 18:06:43.000000', '2026-01-24', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 22, b'0'),
(534, '2025-12-24 18:06:43.000000', '2025-12-24', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(535, '2025-12-24 18:06:43.000000', '2025-12-25', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(536, '2025-12-24 18:06:43.000000', '2025-12-26', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(537, '2025-12-24 18:06:43.000000', '2025-12-27', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(538, '2025-12-24 18:06:43.000000', '2025-12-28', '19:00:00.000000', '10:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 23, b'0'),
(539, '2025-12-24 18:06:43.000000', '2025-12-29', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(540, '2025-12-24 18:06:43.000000', '2025-12-30', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(541, '2025-12-24 18:06:43.000000', '2025-12-31', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(542, '2025-12-24 18:06:43.000000', '2026-01-01', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(543, '2025-12-24 18:06:43.000000', '2026-01-02', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(544, '2025-12-24 18:06:43.000000', '2026-01-03', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(545, '2025-12-24 18:06:43.000000', '2026-01-04', '19:00:00.000000', '10:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 23, b'0'),
(546, '2025-12-24 18:06:43.000000', '2026-01-05', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(547, '2025-12-24 18:06:43.000000', '2026-01-06', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(548, '2025-12-24 18:06:43.000000', '2026-01-07', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(549, '2025-12-24 18:06:43.000000', '2026-01-08', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(550, '2025-12-24 18:06:43.000000', '2026-01-09', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(551, '2025-12-24 18:06:43.000000', '2026-01-10', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(552, '2025-12-24 18:06:43.000000', '2026-01-11', '19:00:00.000000', '10:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 23, b'0'),
(553, '2025-12-24 18:06:43.000000', '2026-01-12', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(554, '2025-12-24 18:06:43.000000', '2026-01-13', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(555, '2025-12-24 18:06:43.000000', '2026-01-14', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(556, '2025-12-24 18:06:43.000000', '2026-01-15', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(557, '2025-12-24 18:06:43.000000', '2026-01-16', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(558, '2025-12-24 18:06:43.000000', '2026-01-17', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(559, '2025-12-24 18:06:43.000000', '2026-01-18', '19:00:00.000000', '10:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 23, b'0'),
(560, '2025-12-24 18:06:43.000000', '2026-01-19', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(561, '2025-12-24 18:06:43.000000', '2026-01-20', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(562, '2025-12-24 18:06:43.000000', '2026-01-21', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(563, '2025-12-24 18:06:44.000000', '2026-01-22', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(564, '2025-12-24 18:06:44.000000', '2026-01-23', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(565, '2025-12-24 18:06:44.000000', '2026-01-24', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 23, b'0'),
(566, '2025-12-24 18:06:44.000000', '2025-12-24', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(567, '2025-12-24 18:06:44.000000', '2025-12-25', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(568, '2025-12-24 18:06:44.000000', '2025-12-26', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(569, '2025-12-24 18:06:44.000000', '2025-12-27', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(570, '2025-12-24 18:06:44.000000', '2025-12-28', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 24, b'0'),
(571, '2025-12-24 18:06:44.000000', '2025-12-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(572, '2025-12-24 18:06:44.000000', '2025-12-30', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(573, '2025-12-24 18:06:44.000000', '2025-12-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(574, '2025-12-24 18:06:44.000000', '2026-01-01', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(575, '2025-12-24 18:06:44.000000', '2026-01-02', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(576, '2025-12-24 18:06:44.000000', '2026-01-03', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(577, '2025-12-24 18:06:44.000000', '2026-01-04', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 24, b'0'),
(578, '2025-12-24 18:06:44.000000', '2026-01-05', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(579, '2025-12-24 18:06:44.000000', '2026-01-06', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(580, '2025-12-24 18:06:44.000000', '2026-01-07', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(581, '2025-12-24 18:06:44.000000', '2026-01-08', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(582, '2025-12-24 18:06:44.000000', '2026-01-09', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(583, '2025-12-24 18:06:44.000000', '2026-01-10', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(584, '2025-12-24 18:06:44.000000', '2026-01-11', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 24, b'0'),
(585, '2025-12-24 18:06:44.000000', '2026-01-12', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(586, '2025-12-24 18:06:44.000000', '2026-01-13', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(587, '2025-12-24 18:06:44.000000', '2026-01-14', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(588, '2025-12-24 18:06:44.000000', '2026-01-15', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(589, '2025-12-24 18:06:44.000000', '2026-01-16', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(590, '2025-12-24 18:06:44.000000', '2026-01-17', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(591, '2025-12-24 18:06:44.000000', '2026-01-18', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 24, b'0'),
(592, '2025-12-24 18:06:44.000000', '2026-01-19', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(593, '2025-12-24 18:06:44.000000', '2026-01-20', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(594, '2025-12-24 18:06:44.000000', '2026-01-21', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(595, '2025-12-24 18:06:44.000000', '2026-01-22', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(596, '2025-12-24 18:06:44.000000', '2026-01-23', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(597, '2025-12-24 18:06:44.000000', '2026-01-24', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 24, b'0'),
(598, '2025-12-24 18:06:44.000000', '2025-12-24', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 25, b'0'),
(599, '2025-12-24 18:06:44.000000', '2025-12-25', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 25, b'0'),
(600, '2025-12-24 18:06:44.000000', '2025-12-26', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(601, '2025-12-24 18:06:44.000000', '2025-12-27', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(602, '2025-12-24 18:06:44.000000', '2025-12-28', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(603, '2025-12-24 18:06:44.000000', '2025-12-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(604, '2025-12-24 18:06:44.000000', '2025-12-30', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(605, '2025-12-24 18:06:44.000000', '2025-12-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(606, '2025-12-24 18:06:44.000000', '2026-01-01', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(607, '2025-12-24 18:06:44.000000', '2026-01-02', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(608, '2025-12-24 18:06:44.000000', '2026-01-03', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(609, '2025-12-24 18:06:44.000000', '2026-01-04', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(610, '2025-12-24 18:06:44.000000', '2026-01-05', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(611, '2025-12-24 18:06:44.000000', '2026-01-06', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(612, '2025-12-24 18:06:44.000000', '2026-01-07', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(613, '2025-12-24 18:06:44.000000', '2026-01-08', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(614, '2025-12-24 18:06:44.000000', '2026-01-09', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(615, '2025-12-24 18:06:44.000000', '2026-01-10', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(616, '2025-12-24 18:06:44.000000', '2026-01-11', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(617, '2025-12-24 18:06:44.000000', '2026-01-12', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(618, '2025-12-24 18:06:44.000000', '2026-01-13', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(619, '2025-12-24 18:06:44.000000', '2026-01-14', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(620, '2025-12-24 18:06:44.000000', '2026-01-15', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(621, '2025-12-24 18:06:44.000000', '2026-01-16', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(622, '2025-12-24 18:06:44.000000', '2026-01-17', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(623, '2025-12-24 18:06:44.000000', '2026-01-18', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(624, '2025-12-24 18:06:44.000000', '2026-01-19', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(625, '2025-12-24 18:06:44.000000', '2026-01-20', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(626, '2025-12-24 18:06:44.000000', '2026-01-21', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(627, '2025-12-24 18:06:44.000000', '2026-01-22', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(628, '2025-12-24 18:06:44.000000', '2026-01-23', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-26 01:00:03.000000', 25, b'0'),
(629, '2025-12-24 18:06:44.000000', '2026-01-24', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-26 01:00:03.000000', 25, b'0'),
(630, '2025-12-24 01:30:27.000000', '2025-12-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-24 18:06:42.000000', 2, b'0'),
(631, '2025-12-26 01:00:00.000000', '2026-01-25', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-29 01:00:00.000000', 5, b'0'),
(632, '2025-12-26 01:00:00.000000', '2026-01-26', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:00.000000', 5, b'0'),
(633, '2025-12-26 01:00:00.000000', '2026-01-25', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-29 01:00:00.000000', 26, b'0'),
(634, '2025-12-26 01:00:00.000000', '2026-01-26', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:00.000000', 26, b'0'),
(635, '2025-12-26 01:00:00.000000', '2026-01-25', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-29 01:00:00.000000', 16, b'0'),
(636, '2025-12-26 01:00:00.000000', '2026-01-26', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-29 01:00:00.000000', 16, b'0'),
(637, '2025-12-26 01:00:01.000000', '2026-01-25', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-29 01:00:00.000000', 30, b'0'),
(638, '2025-12-26 01:00:01.000000', '2026-01-26', '22:00:00.000000', '14:00:00.000000', 'CLOSED', '2025-12-29 01:00:00.000000', 30, b'0'),
(639, '2025-12-26 01:00:01.000000', '2026-01-25', '23:00:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 17, b'0'),
(640, '2025-12-26 01:00:01.000000', '2026-01-26', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 17, b'0'),
(641, '2025-12-26 01:00:01.000000', '2026-01-25', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 18, b'0'),
(642, '2025-12-26 01:00:01.000000', '2026-01-26', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 18, b'0'),
(643, '2025-12-26 01:00:02.000000', '2026-01-25', '17:30:00.000000', '08:30:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 27, b'0'),
(644, '2025-12-26 01:00:02.000000', '2026-01-26', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 27, b'0'),
(645, '2025-12-26 01:00:02.000000', '2026-01-25', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 19, b'0'),
(646, '2025-12-26 01:00:02.000000', '2026-01-26', '18:30:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 19, b'0'),
(647, '2025-12-26 01:00:02.000000', '2026-01-25', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 28, b'0'),
(648, '2025-12-26 01:00:02.000000', '2026-01-26', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 28, b'0'),
(649, '2025-12-26 01:00:02.000000', '2026-01-25', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 20, b'0'),
(650, '2025-12-26 01:00:02.000000', '2026-01-26', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 20, b'0'),
(651, '2025-12-26 01:00:02.000000', '2026-01-25', '20:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 21, b'0'),
(652, '2025-12-26 01:00:02.000000', '2026-01-26', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 21, b'0'),
(653, '2025-12-26 01:00:02.000000', '2026-01-25', '16:00:00.000000', '07:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 29, b'0'),
(654, '2025-12-26 01:00:02.000000', '2026-01-26', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 29, b'0'),
(655, '2025-12-26 01:00:03.000000', '2026-01-25', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 22, b'0'),
(656, '2025-12-26 01:00:03.000000', '2026-01-26', '23:00:00.000000', '16:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 22, b'0'),
(657, '2025-12-26 01:00:03.000000', '2026-01-25', '19:00:00.000000', '10:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 23, b'0'),
(658, '2025-12-26 01:00:03.000000', '2026-01-26', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 23, b'0'),
(659, '2025-12-26 01:00:03.000000', '2026-01-25', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 24, b'0'),
(660, '2025-12-26 01:00:03.000000', '2026-01-26', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 24, b'0'),
(661, '2025-12-26 01:00:03.000000', '2026-01-25', '17:00:00.000000', '08:00:00.000000', 'CLOSED', '2025-12-29 01:00:01.000000', 25, b'0'),
(662, '2025-12-26 01:00:03.000000', '2026-01-26', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2025-12-29 01:00:01.000000', 25, b'0'),
(663, '2025-12-29 01:00:00.000000', '2026-01-27', '17:30:00.000000', '08:00:00.000000', 'CLOSED', '2026-01-04 01:00:00.000000', 5, b'0'),
(664, '2025-12-29 01:00:00.000000', '2026-01-28', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 5, b'0'),
(665, '2025-12-29 01:00:00.000000', '2026-01-29', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 5, b'0'),
(666, '2025-12-29 01:00:00.000000', '2026-01-27', '18:00:00.000000', '09:00:00.000000', 'CLOSED', '2026-01-04 01:00:00.000000', 26, b'0'),
(667, '2025-12-29 01:00:00.000000', '2026-01-28', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 26, b'0'),
(668, '2025-12-29 01:00:00.000000', '2026-01-29', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 26, b'0'),
(669, '2025-12-29 01:00:00.000000', '2026-01-27', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 16, b'0'),
(670, '2025-12-29 01:00:00.000000', '2026-01-28', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 16, b'0'),
(671, '2025-12-29 01:00:00.000000', '2026-01-29', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 16, b'0'),
(672, '2025-12-29 01:00:00.000000', '2026-01-27', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 30, b'0'),
(673, '2025-12-29 01:00:00.000000', '2026-01-28', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 30, b'0'),
(674, '2025-12-29 01:00:00.000000', '2026-01-29', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 30, b'0'),
(675, '2025-12-29 01:00:01.000000', '2026-01-27', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 17, b'0'),
(676, '2025-12-29 01:00:01.000000', '2026-01-28', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 17, b'0'),
(677, '2025-12-29 01:00:01.000000', '2026-01-29', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 17, b'0'),
(678, '2025-12-29 01:00:01.000000', '2026-01-27', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 18, b'0'),
(679, '2025-12-29 01:00:01.000000', '2026-01-28', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 18, b'0'),
(680, '2025-12-29 01:00:01.000000', '2026-01-29', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:00.000000', 18, b'0'),
(681, '2025-12-29 01:00:01.000000', '2026-01-27', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 27, b'0'),
(682, '2025-12-29 01:00:01.000000', '2026-01-28', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 27, b'0'),
(683, '2025-12-29 01:00:01.000000', '2026-01-29', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 27, b'0'),
(684, '2025-12-29 01:00:01.000000', '2026-01-27', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 19, b'0'),
(685, '2025-12-29 01:00:01.000000', '2026-01-28', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 19, b'0'),
(686, '2025-12-29 01:00:01.000000', '2026-01-29', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 19, b'0'),
(687, '2025-12-29 01:00:01.000000', '2026-01-27', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 28, b'0'),
(688, '2025-12-29 01:00:01.000000', '2026-01-28', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 28, b'0'),
(689, '2025-12-29 01:00:01.000000', '2026-01-29', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 28, b'0'),
(690, '2025-12-29 01:00:01.000000', '2026-01-27', '23:30:00.000000', '15:00:00.000000', 'CLOSED', '2026-01-04 01:00:01.000000', 20, b'0'),
(691, '2025-12-29 01:00:01.000000', '2026-01-28', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 20, b'0'),
(692, '2025-12-29 01:00:01.000000', '2026-01-29', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 20, b'0'),
(693, '2025-12-29 01:00:01.000000', '2026-01-27', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 21, b'0'),
(694, '2025-12-29 01:00:01.000000', '2026-01-28', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 21, b'0'),
(695, '2025-12-29 01:00:01.000000', '2026-01-29', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 21, b'0'),
(696, '2025-12-29 01:00:01.000000', '2026-01-27', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 29, b'0'),
(697, '2025-12-29 01:00:01.000000', '2026-01-28', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 29, b'0'),
(698, '2025-12-29 01:00:01.000000', '2026-01-29', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 29, b'0'),
(699, '2025-12-29 01:00:01.000000', '2026-01-27', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 22, b'0'),
(700, '2025-12-29 01:00:01.000000', '2026-01-28', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 22, b'0'),
(701, '2025-12-29 01:00:01.000000', '2026-01-29', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 22, b'0'),
(702, '2025-12-29 01:00:01.000000', '2026-01-27', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 23, b'0'),
(703, '2025-12-29 01:00:01.000000', '2026-01-28', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 23, b'0'),
(704, '2025-12-29 01:00:01.000000', '2026-01-29', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 23, b'0'),
(705, '2025-12-29 01:00:01.000000', '2026-01-27', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 24, b'0'),
(706, '2025-12-29 01:00:01.000000', '2026-01-28', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 24, b'0'),
(707, '2025-12-29 01:00:01.000000', '2026-01-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 24, b'0'),
(708, '2025-12-29 01:00:01.000000', '2026-01-27', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 25, b'0'),
(709, '2025-12-29 01:00:01.000000', '2026-01-28', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 25, b'0'),
(710, '2025-12-29 01:00:01.000000', '2026-01-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', '2026-01-04 01:00:01.000000', 25, b'0'),
(807, '2026-01-04 01:00:00.000000', '2026-01-25', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 2, b'0'),
(808, '2026-01-04 01:00:00.000000', '2026-01-26', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(809, '2026-01-04 01:00:00.000000', '2026-01-27', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 2, b'0'),
(810, '2026-01-04 01:00:00.000000', '2026-01-28', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(811, '2026-01-04 01:00:00.000000', '2026-01-29', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(812, '2026-01-04 01:00:00.000000', '2026-01-30', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(813, '2026-01-04 01:00:00.000000', '2026-01-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(814, '2026-01-04 01:00:00.000000', '2026-02-01', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 2, b'0'),
(815, '2026-01-04 01:00:00.000000', '2026-02-02', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(816, '2026-01-04 01:00:00.000000', '2026-02-03', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 2, b'0'),
(817, '2026-01-04 01:00:00.000000', '2026-02-04', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 2, b'0'),
(818, '2026-01-04 01:00:00.000000', '2026-01-30', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 5, b'0'),
(819, '2026-01-04 01:00:00.000000', '2026-01-31', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 5, b'0'),
(820, '2026-01-04 01:00:00.000000', '2026-02-01', '17:30:00.000000', '08:00:00.000000', 'CLOSED', NULL, 5, b'0'),
(821, '2026-01-04 01:00:00.000000', '2026-02-02', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 5, b'0'),
(822, '2026-01-04 01:00:00.000000', '2026-02-03', '17:30:00.000000', '08:00:00.000000', 'CLOSED', NULL, 5, b'0'),
(823, '2026-01-04 01:00:00.000000', '2026-02-04', '17:30:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 5, b'0'),
(824, '2026-01-04 01:00:00.000000', '2026-01-30', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 26, b'0'),
(825, '2026-01-04 01:00:00.000000', '2026-01-31', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 26, b'0'),
(826, '2026-01-04 01:00:00.000000', '2026-02-01', '18:00:00.000000', '09:00:00.000000', 'CLOSED', NULL, 26, b'0'),
(827, '2026-01-04 01:00:00.000000', '2026-02-02', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 26, b'0'),
(828, '2026-01-04 01:00:00.000000', '2026-02-03', '18:00:00.000000', '09:00:00.000000', 'CLOSED', NULL, 26, b'0'),
(829, '2026-01-04 01:00:00.000000', '2026-02-04', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 26, b'0'),
(830, '2026-01-04 01:00:00.000000', '2026-01-30', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 16, b'0'),
(831, '2026-01-04 01:00:00.000000', '2026-01-31', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 16, b'0'),
(832, '2026-01-04 01:00:00.000000', '2026-02-01', '22:00:00.000000', '14:00:00.000000', 'CLOSED', NULL, 16, b'0'),
(833, '2026-01-04 01:00:00.000000', '2026-02-02', '22:00:00.000000', '14:00:00.000000', 'CLOSED', NULL, 16, b'0'),
(834, '2026-01-04 01:00:00.000000', '2026-02-03', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 16, b'0'),
(835, '2026-01-04 01:00:00.000000', '2026-02-04', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 16, b'0'),
(836, '2026-01-04 01:00:00.000000', '2026-01-30', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 30, b'0'),
(837, '2026-01-04 01:00:00.000000', '2026-01-31', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 30, b'0'),
(838, '2026-01-04 01:00:00.000000', '2026-02-01', '22:00:00.000000', '14:00:00.000000', 'CLOSED', NULL, 30, b'0'),
(839, '2026-01-04 01:00:00.000000', '2026-02-02', '22:00:00.000000', '14:00:00.000000', 'CLOSED', NULL, 30, b'0'),
(840, '2026-01-04 01:00:00.000000', '2026-02-03', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 30, b'0'),
(841, '2026-01-04 01:00:00.000000', '2026-02-04', '22:00:00.000000', '14:00:00.000000', 'AVAILABLE', NULL, 30, b'0'),
(842, '2026-01-04 01:00:00.000000', '2026-01-30', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(843, '2026-01-04 01:00:00.000000', '2026-01-31', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(844, '2026-01-04 01:00:00.000000', '2026-02-01', '23:00:00.000000', '15:00:00.000000', 'CLOSED', NULL, 17, b'0'),
(845, '2026-01-04 01:00:00.000000', '2026-02-02', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(846, '2026-01-04 01:00:00.000000', '2026-02-03', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(847, '2026-01-04 01:00:00.000000', '2026-02-04', '23:00:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 17, b'0'),
(848, '2026-01-04 01:00:00.000000', '2026-01-30', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(849, '2026-01-04 01:00:00.000000', '2026-01-31', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(850, '2026-01-04 01:00:00.000000', '2026-02-01', '18:00:00.000000', '09:00:00.000000', 'CLOSED', NULL, 18, b'0'),
(851, '2026-01-04 01:00:00.000000', '2026-02-02', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(852, '2026-01-04 01:00:00.000000', '2026-02-03', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(853, '2026-01-04 01:00:00.000000', '2026-02-04', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 18, b'0'),
(854, '2026-01-04 01:00:01.000000', '2026-01-30', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0'),
(855, '2026-01-04 01:00:01.000000', '2026-01-31', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0'),
(856, '2026-01-04 01:00:01.000000', '2026-02-01', '17:30:00.000000', '08:30:00.000000', 'CLOSED', NULL, 27, b'0'),
(857, '2026-01-04 01:00:01.000000', '2026-02-02', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0');
INSERT INTO `staff_availabilities` (`id`, `created_at`, `date`, `end_time`, `start_time`, `status`, `updated_at`, `staff_id`, `user_edited`) VALUES
(858, '2026-01-04 01:00:01.000000', '2026-02-03', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0'),
(859, '2026-01-04 01:00:01.000000', '2026-02-04', '17:30:00.000000', '08:30:00.000000', 'AVAILABLE', NULL, 27, b'0'),
(860, '2026-01-04 01:00:01.000000', '2026-01-30', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 19, b'0'),
(861, '2026-01-04 01:00:01.000000', '2026-01-31', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 19, b'0'),
(862, '2026-01-04 01:00:01.000000', '2026-02-01', '18:30:00.000000', '09:00:00.000000', 'CLOSED', NULL, 19, b'0'),
(863, '2026-01-04 01:00:01.000000', '2026-02-02', '18:30:00.000000', '09:00:00.000000', 'CLOSED', NULL, 19, b'0'),
(864, '2026-01-04 01:00:01.000000', '2026-02-03', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 19, b'0'),
(865, '2026-01-04 01:00:01.000000', '2026-02-04', '18:30:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 19, b'0'),
(866, '2026-01-04 01:00:01.000000', '2026-01-30', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 28, b'0'),
(867, '2026-01-04 01:00:01.000000', '2026-01-31', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 28, b'0'),
(868, '2026-01-04 01:00:01.000000', '2026-02-01', '18:00:00.000000', '09:00:00.000000', 'CLOSED', NULL, 28, b'0'),
(869, '2026-01-04 01:00:01.000000', '2026-02-02', '18:00:00.000000', '09:00:00.000000', 'CLOSED', NULL, 28, b'0'),
(870, '2026-01-04 01:00:01.000000', '2026-02-03', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 28, b'0'),
(871, '2026-01-04 01:00:01.000000', '2026-02-04', '18:00:00.000000', '09:00:00.000000', 'AVAILABLE', NULL, 28, b'0'),
(872, '2026-01-04 01:00:01.000000', '2026-01-30', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 20, b'0'),
(873, '2026-01-04 01:00:01.000000', '2026-01-31', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 20, b'0'),
(874, '2026-01-04 01:00:01.000000', '2026-02-01', '23:30:00.000000', '15:00:00.000000', 'CLOSED', NULL, 20, b'0'),
(875, '2026-01-04 01:00:01.000000', '2026-02-02', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 20, b'0'),
(876, '2026-01-04 01:00:01.000000', '2026-02-03', '23:30:00.000000', '15:00:00.000000', 'CLOSED', NULL, 20, b'0'),
(877, '2026-01-04 01:00:01.000000', '2026-02-04', '23:30:00.000000', '15:00:00.000000', 'AVAILABLE', NULL, 20, b'0'),
(878, '2026-01-04 01:00:01.000000', '2026-01-30', '20:00:00.000000', '07:00:00.000000', 'CLOSED', NULL, 21, b'0'),
(879, '2026-01-04 01:00:01.000000', '2026-01-31', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 21, b'0'),
(880, '2026-01-04 01:00:01.000000', '2026-02-01', '20:00:00.000000', '07:00:00.000000', 'CLOSED', NULL, 21, b'0'),
(881, '2026-01-04 01:00:01.000000', '2026-02-02', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 21, b'0'),
(882, '2026-01-04 01:00:01.000000', '2026-02-03', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 21, b'0'),
(883, '2026-01-04 01:00:01.000000', '2026-02-04', '20:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 21, b'0'),
(884, '2026-01-04 01:00:01.000000', '2026-01-30', '16:00:00.000000', '07:00:00.000000', 'CLOSED', NULL, 29, b'0'),
(885, '2026-01-04 01:00:01.000000', '2026-01-31', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 29, b'0'),
(886, '2026-01-04 01:00:01.000000', '2026-02-01', '16:00:00.000000', '07:00:00.000000', 'CLOSED', NULL, 29, b'0'),
(887, '2026-01-04 01:00:01.000000', '2026-02-02', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 29, b'0'),
(888, '2026-01-04 01:00:01.000000', '2026-02-03', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 29, b'0'),
(889, '2026-01-04 01:00:01.000000', '2026-02-04', '16:00:00.000000', '07:00:00.000000', 'AVAILABLE', NULL, 29, b'0'),
(890, '2026-01-04 01:00:01.000000', '2026-01-30', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', NULL, 22, b'0'),
(891, '2026-01-04 01:00:01.000000', '2026-01-31', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', NULL, 22, b'0'),
(892, '2026-01-04 01:00:01.000000', '2026-02-01', '23:00:00.000000', '16:00:00.000000', 'CLOSED', NULL, 22, b'0'),
(893, '2026-01-04 01:00:01.000000', '2026-02-02', '23:00:00.000000', '16:00:00.000000', 'CLOSED', NULL, 22, b'0'),
(894, '2026-01-04 01:00:01.000000', '2026-02-03', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', NULL, 22, b'0'),
(895, '2026-01-04 01:00:01.000000', '2026-02-04', '23:00:00.000000', '16:00:00.000000', 'AVAILABLE', NULL, 22, b'0'),
(896, '2026-01-04 01:00:01.000000', '2026-01-30', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(897, '2026-01-04 01:00:01.000000', '2026-01-31', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(898, '2026-01-04 01:00:01.000000', '2026-02-01', '19:00:00.000000', '10:00:00.000000', 'CLOSED', NULL, 23, b'0'),
(899, '2026-01-04 01:00:01.000000', '2026-02-02', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(900, '2026-01-04 01:00:01.000000', '2026-02-03', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(901, '2026-01-04 01:00:01.000000', '2026-02-04', '19:00:00.000000', '10:00:00.000000', 'AVAILABLE', NULL, 23, b'0'),
(902, '2026-01-04 01:00:01.000000', '2026-01-30', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(903, '2026-01-04 01:00:01.000000', '2026-01-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(904, '2026-01-04 01:00:01.000000', '2026-02-01', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 24, b'0'),
(905, '2026-01-04 01:00:01.000000', '2026-02-02', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(906, '2026-01-04 01:00:01.000000', '2026-02-03', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(907, '2026-01-04 01:00:01.000000', '2026-02-04', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 24, b'0'),
(908, '2026-01-04 01:00:01.000000', '2026-01-30', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 25, b'0'),
(909, '2026-01-04 01:00:01.000000', '2026-01-31', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 25, b'0'),
(910, '2026-01-04 01:00:01.000000', '2026-02-01', '17:00:00.000000', '08:00:00.000000', 'CLOSED', NULL, 25, b'0'),
(911, '2026-01-04 01:00:01.000000', '2026-02-02', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 25, b'0'),
(912, '2026-01-04 01:00:01.000000', '2026-02-03', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 25, b'0'),
(913, '2026-01-04 01:00:01.000000', '2026-02-04', '17:00:00.000000', '08:00:00.000000', 'AVAILABLE', NULL, 25, b'0');

-- --------------------------------------------------------

--
-- Table structure for table `subscriptions`
--

CREATE TABLE `subscriptions` (
  `id` bigint(20) NOT NULL,
  `cancelled_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `end_date` date DEFAULT NULL,
  `plan_name` tinyint(4) NOT NULL,
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
(1, 'https://res.cloudinary.com/duvougrqx/image/upload/v1767364523/Bookify/StoonProd-user-1.avif', '2025-10-30 18:12:43.000000', 'amirghodhben2.0@gmail.com', 'amir', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21680138013', 'BUSINESS_OWNER', 'VERIFIED', '2026-01-02 14:35:23.000000'),
(2, 'https://c8.alamy.com/comp/2J3B2T7/3d-illustration-of-smiling-businessman-close-up-portrait-cute-cartoon-man-avatar-character-face-isolated-on-white-background-2J3B2T7.jpg', '2025-11-25 19:53:25.000000', 'dissojak@icloud.com', 'disso mac Staff', '$2a$10$3zv9AlEr5wQ9iM4q9UbcUumRYUxx/SXIoD5tGBtoWmyCDbldZa2d2', NULL, NULL, '+21623039320', 'STAFF', 'VERIFIED', '2025-11-26 21:28:17.000000'),
(3, 'https://png.pngtree.com/png-clipart/20230927/original/pngtree-man-avatar-image-for-profile-png-image_13001882.png', '2025-11-25 19:55:00.000000', 'dissojak@gmail.com', 'disso mac Client', '$2a$10$9sY.bZ.sIU5qL7zC./Jq1O.baq9I9Mv4gmedk.h0sh3hYCbrvaPEG', NULL, NULL, '+21623039320', 'CLIENT', 'VERIFIED', '2025-12-23 22:03:23.000000'),
(4, 'https://example.com/avatars/admin.jpg', '2025-11-25 19:55:48.000000', 'admin@example.com', 'Admin User', '$2a$10$7fVU6aNkqykITEorIh2DHeBnPEhPLztLxQpeWmBoxYJ1YpyoyB8Ou', NULL, NULL, '+33123456789', 'ADMIN', 'VERIFIED', NULL),
(5, 'https://png.pngtree.com/png-clipart/20230927/original/pngtree-man-avatar-image-for-profile-png-image_13001877.png', '2025-11-25 19:57:32.000000', 'therealstoon@gmail.com', 'TheRealStoon mac', '$2a$10$Skm5ljinLBCCXGgkpuUMP.0R3t8RDH.gxzbbCsQatlumGc3BGHDYe', NULL, NULL, '+21623039320', 'STAFF', 'VERIFIED', '2025-11-26 21:20:08.000000'),
(6, 'https://res.cloudinary.com/duvougrqx/image/upload/v1766762155/Bookify/StoonProd-user-6.svg', '2025-11-26 20:35:51.000000', 'stoonproduction@gmail.com', 'Stoon Prod', '$2a$10$L4sXEahryFTJxhenG2egueiiaHrs97QhkS1oeB/.H3m0VHvxN1QF.', NULL, NULL, '+21623039320', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-26 15:15:57.000000'),
(7, 'https://res.cloudinary.com/duvougrqx/image/upload/v1766761870/Bookify/StoonProd-user-7.png', '2025-12-23 15:30:39.000000', 'ademadembenamor@hotmail.com', 'Emna Gmati', '$2a$10$.dO/H6zd0oiwCpOlP.jIV.JirdR2JP9n8Z0enC4OVZKENpBLkQTLC', NULL, NULL, '+216 95132694', 'CLIENT', 'VERIFIED', '2026-01-03 22:00:45.000000'),
(8, 'https://randomuser.me/api/portraits/men/75.jpg', '2025-10-10 10:00:00.000000', 'mario.rossi@bellaitalia.fr', 'Mario Rossi', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33142567890', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-10 14:00:00.000000'),
(9, 'https://randomuser.me/api/portraits/men/60.jpg', '2025-09-15 09:00:00.000000', 'ali.sarrar@darsarrar.tn', 'Ali Sarrar', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21671234567', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-08 16:00:00.000000'),
(10, 'https://randomuser.me/api/portraits/women/68.jpg', '2025-10-25 11:00:00.000000', 'lina.bouzid@glamourhair.com', 'Lina Bouzid', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21698765432', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-15 10:00:00.000000'),
(11, 'https://randomuser.me/api/portraits/men/35.jpg', '2025-08-05 09:30:00.000000', 'nicolas.dupont@urbancuts.com', 'Nicolas Dupont', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33478901234', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-12 11:00:00.000000'),
(12, 'https://randomuser.me/api/portraits/men/88.jpg', '2025-07-01 16:00:00.000000', 'kenji.tanaka@sakuratokyo.fr', 'Kenji Tanaka', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33145678901', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-11 18:00:00.000000'),
(13, 'https://randomuser.me/api/portraits/women/25.jpg', '2025-06-10 12:00:00.000000', 'nadia.amor@fitlifestudio.tn', 'Nadia Amor', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21655443322', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-13 08:00:00.000000'),
(14, 'https://randomuser.me/api/portraits/men/42.jpg', '2025-05-18 14:30:00.000000', 'luigi.napoli@pizzanapoletana.fr', 'Luigi Napoli', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33149876543', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-09 17:00:00.000000'),
(15, 'https://randomuser.me/api/portraits/women/55.jpg', '2025-04-22 11:00:00.000000', 'yasmine.ben@zenspacenter. tn', 'Yasmine Ben Salem', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21670112233', 'BUSINESS_OWNER', 'VERIFIED', '2025-12-07 13:00:00.000000'),
(16, 'https://randomuser.me/api/portraits/women/33.jpg', '2025-11-05 10:30:00.000000', 'sarah.jones@email.com', 'Sarah Jones', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33609887766', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(17, 'https://randomuser.me/api/portraits/men/28.jpg', '2025-11-08 14:00:00.000000', 'karim.slimani@email.com', 'Karim Slimani', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21699123456', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(18, 'https://randomuser.me/api/portraits/women/77.jpg', '2025-10-15 16:00:00.000000', 'alice.petit@email.fr', 'Alice Petit', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33601122334', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(19, 'https://randomuser.me/api/portraits/men/29.jpg', '2025-09-20 11:00:00.000000', 'mohamed.brahmi@email.com', 'Mohamed Brahmi', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21620998877', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(20, 'https://randomuser.me/api/portraits/women/88.jpg', '2025-08-15 15:30:00.000000', 'fatima.hamdi@email. com', 'Fatima Hamdi', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21655887766', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(21, 'https://randomuser.me/api/portraits/men/55.jpg', '2025-11-12 09:00:00.000000', 'pierre.martin@email.fr', 'Pierre Martin', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33607123456', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(22, 'https://randomuser.me/api/portraits/women/22.jpg', '2025-11-15 13:00:00.000000', 'leila.ben@email.com', 'Leila Ben Ali', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21698334455', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(23, 'https://randomuser.me/api/portraits/men/66.jpg', '2025-11-18 10:30:00.000000', 'jean.dubois@email.fr', 'Jean Dubois', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33609998877', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(24, 'https://randomuser.me/api/portraits/women/40.jpg', '2025-11-20 14:00:00.000000', 'amira.ktari@email.com', 'Amira Ktari', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21655667788', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(25, 'https://randomuser.me/api/portraits/men/38.jpg', '2025-11-22 16:00:00.000000', 'ahmed.najjar@email.com', 'Ahmed Najjar', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21620556677', 'STAFF', 'VERIFIED', '2025-12-24 18:00:00.000000'),
(26, 'https://randomuser.me/api/portraits/men/19.jpg', '2025-11-26 08:00:00.000000', 'hamza.barber@stoonbarber.com', 'Hamza Cherif', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21698223344', 'STAFF', 'VERIFIED', '2025-12-10 09:00:00.000000'),
(27, 'https://randomuser.me/api/portraits/women/30.jpg', '2025-11-27 09:00:00.000000', 'salma.stylist@glamourhair.com', 'Salma Agrebi', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21699445566', 'STAFF', 'VERIFIED', '2025-12-11 10:00:00.000000'),
(28, 'https://randomuser.me/api/portraits/men/24.jpg', '2025-11-28 10:00:00.000000', 'youssef.barber@urbancuts.com', 'Youssef Mansour', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33607334455', 'STAFF', 'VERIFIED', '2025-12-08 11:00:00.000000'),
(29, 'https://randomuser.me/api/portraits/women/18.jpg', '2025-11-29 11:00:00.000000', 'ines.trainer@fitlifestudio.tn', 'Ines Gharbi', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+21655998877', 'STAFF', 'VERIFIED', '2025-12-09 12:00:00.000000'),
(30, 'https://randomuser.me/api/portraits/men/41.jpg', '2025-11-30 12:00:00.000000', 'mehdi.chef@bellaitalia.fr', 'Mehdi Tlili', '$2a$10$EW9L6l.tpKPN1mxgyAsdQO3I/mWB.bCuOrj7yNrGnNE.HQOGH6yxG', NULL, NULL, '+33608776655', 'STAFF', 'VERIFIED', '2025-12-06 13:00:00.000000');

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
-- Indexes for table `business_clients`
--
ALTER TABLE `business_clients`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_business_client_phone` (`business_id`,`phone`);

--
-- Indexes for table `business_evaluations`
--
ALTER TABLE `business_evaluations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_eval_business` (`business_id`);

--
-- Indexes for table `business_images`
--
ALTER TABLE `business_images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_business_image_business` (`business_id`);

--
-- Indexes for table `business_ratings`
--
ALTER TABLE `business_ratings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKdi5ag3hnfia75c38b8n0u9d98` (`business_id`),
  ADD KEY `FKtp2yfws8kctdgl1njrh5f1mij` (`client_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`),
  ADD KEY `fk_category_created_by` (`created_by`);

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
  ADD KEY `FK7ad6rnmb063euhsqy0bgjfk0g` (`client_id`),
  ADD KEY `FKckiv8u2mpj223y0th29xrost5` (`resource_id`);

--
-- Indexes for table `resource_reservations`
--
ALTER TABLE `resource_reservations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK938p5d4n34777gj8jbukme8fb` (`client_id`),
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
  ADD KEY `FKryfh22ccvq43d77rj8d6nfrk8` (`business_id`),
  ADD KEY `fk_service_created_by` (`created_by`);

--
-- Indexes for table `service_bookings`
--
ALTER TABLE `service_bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK9ybrxpcjjmqm7p3lqo1sfrb1e` (`client_id`),
  ADD KEY `FK1cyr30xgaheo32v5iha15mvfn` (`service_id`),
  ADD KEY `FKobkxb0byfe0oq2tynu2e85h01` (`staff_id`),
  ADD KEY `FKd5s1pkxqucj9g6sxd3djgsyiy` (`business_client_id`);

--
-- Indexes for table `service_ratings`
--
ALTER TABLE `service_ratings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKdlunwaiw7ktcbcr4rvejtjd74` (`client_id`),
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
  ADD KEY `fk_staff_business` (`business_id`);

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
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `businesses`
--
ALTER TABLE `businesses`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `business_clients`
--
ALTER TABLE `business_clients`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `business_evaluations`
--
ALTER TABLE `business_evaluations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `business_images`
--
ALTER TABLE `business_images`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ratings`
--
ALTER TABLE `ratings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

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
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- AUTO_INCREMENT for table `staff_availabilities`
--
ALTER TABLE `staff_availabilities`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=914;

--
-- AUTO_INCREMENT for table `subscriptions`
--
ALTER TABLE `subscriptions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `activation_tokens`
--
ALTER TABLE `activation_tokens`
  ADD CONSTRAINT `fk_activation_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `businesses`
--
ALTER TABLE `businesses`
  ADD CONSTRAINT `fk_business_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  ADD CONSTRAINT `fk_business_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `business_clients`
--
ALTER TABLE `business_clients`
  ADD CONSTRAINT `fk_business_client_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

--
-- Constraints for table `business_evaluations`
--
ALTER TABLE `business_evaluations`
  ADD CONSTRAINT `fk_eval_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

--
-- Constraints for table `business_images`
--
ALTER TABLE `business_images`
  ADD CONSTRAINT `fk_business_image_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

--
-- Constraints for table `business_ratings`
--
ALTER TABLE `business_ratings`
  ADD CONSTRAINT `FK3sb0qmu96yiuh6tgh56xa1fn3` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`),
  ADD CONSTRAINT `FKdi5ag3hnfia75c38b8n0u9d98` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`),
  ADD CONSTRAINT `FKtp2yfws8kctdgl1njrh5f1mij` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `fk_category_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

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
  ADD CONSTRAINT `FK7ad6rnmb063euhsqy0bgjfk0g` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKckiv8u2mpj223y0th29xrost5` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`id`),
  ADD CONSTRAINT `FKgxn9yqmios192clsgokw20fi8` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`);

--
-- Constraints for table `resource_reservations`
--
ALTER TABLE `resource_reservations`
  ADD CONSTRAINT `FK938p5d4n34777gj8jbukme8fb` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKgv5iq0d0eoxctbo01ma39sd8y` FOREIGN KEY (`resource_availability_id`) REFERENCES `resource_availabilities` (`id`),
  ADD CONSTRAINT `FKn37j36nancpcmt0d3p6u9rxfr` FOREIGN KEY (`id`) REFERENCES `bookings` (`id`);

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `FK28an517hrxtt2bsg93uefugrm` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

--
-- Constraints for table `services`
--
ALTER TABLE `services`
  ADD CONSTRAINT `FKryfh22ccvq43d77rj8d6nfrk8` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`),
  ADD CONSTRAINT `fk_service_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`);

--
-- Constraints for table `service_bookings`
--
ALTER TABLE `service_bookings`
  ADD CONSTRAINT `FK1cyr30xgaheo32v5iha15mvfn` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  ADD CONSTRAINT `FK6t4e5id96v16yj88y4vnipeqp` FOREIGN KEY (`id`) REFERENCES `bookings` (`id`),
  ADD CONSTRAINT `FK9ybrxpcjjmqm7p3lqo1sfrb1e` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKd5s1pkxqucj9g6sxd3djgsyiy` FOREIGN KEY (`business_client_id`) REFERENCES `business_clients` (`id`),
  ADD CONSTRAINT `FKobkxb0byfe0oq2tynu2e85h01` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`);

--
-- Constraints for table `service_ratings`
--
ALTER TABLE `service_ratings`
  ADD CONSTRAINT `FKdlunwaiw7ktcbcr4rvejtjd74` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
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
  ADD CONSTRAINT `FK5aes4ihkx95t5h3fvhayg940u` FOREIGN KEY (`id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_staff_business` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`);

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
