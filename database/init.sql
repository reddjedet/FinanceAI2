-- Inicialización de bases de datos para microservicios FinanceAI v2

CREATE DATABASE IF NOT EXISTS `financeai_auth` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `financeai_transactions` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Otorgar permisos al usuario de desarrollo
GRANT ALL PRIVILEGES ON `financeai_auth`.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON `financeai_transactions`.* TO 'dev_user'@'%';

FLUSH PRIVILEGES;
