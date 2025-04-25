-- Création de la base de données
CREATE DATABASE IF NOT EXISTS mis_db;
USE mis_db;

-- Table des utilisateurs
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table des catégories
CREATE TABLE IF NOT EXISTS categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Table des produits
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Table des commandes
CREATE TABLE IF NOT EXISTS orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Table des détails de commande
CREATE TABLE IF NOT EXISTS order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Insertion de quelques données de test pour les catégories
INSERT INTO categories (name, description) VALUES
('Électronique', 'Produits électroniques et gadgets'),
('Vêtements', 'Vêtements et accessoires de mode'),
('Livres', 'Livres et publications'),
('Sports', 'Équipements et accessoires de sport');

-- Insertion de quelques données de test pour les produits
INSERT INTO products (name, description, price, stock_quantity, category_id) VALUES
('Smartphone XYZ', 'Dernier modèle de smartphone', 699.99, 50, 1),
('T-shirt Basic', 'T-shirt en coton 100%', 19.99, 100, 2),
('Le Guide du développeur', 'Guide complet de programmation', 49.99, 30, 3),
('Ballon de football', 'Ballon de football professionnel', 29.99, 45, 4); 