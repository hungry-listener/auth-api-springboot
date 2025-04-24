INSERT INTO `roles` (`id`, `name`, `description`, `created_at`) VALUES
(1, 'admin', 'Has full access to all features and settings. Can manage users and content.', '2025-04-01 12:40:37'),
(2, 'editor', 'Can review, edit, and publish content created by authors and contributors.', '2025-04-01 12:40:37'),
(3, 'author', 'Can create and manage their own content, but cannot publish without approval.', '2025-04-01 12:40:37'),
(4, 'contributor', 'Can write and submit content for review but cannot publish it.', '2025-04-01 12:40:37'),
(5, 'subscriber', 'Can access restricted content but cannot create or manage content.', '2025-04-01 12:40:37'),
(6, 'viewer', 'Has basic access to public content without additional privileges.', '2025-04-01 12:40:37');