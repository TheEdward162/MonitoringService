DROP DATABASE IF EXISTS applifting_monitoring_service;
CREATE DATABASE applifting_monitoring_service;

DROP USER 'applifting'@'%';
CREATE USER 'applifting'@'%' IDENTIFIED BY 'password';

GRANT INSERT ON applifting_monitoring_service.* TO 'applifting'@'%';
GRANT SELECT ON applifting_monitoring_service.* TO 'applifting'@'%';
GRANT UPDATE ON applifting_monitoring_service.* TO 'applifting'@'%';
GRANT DELETE ON applifting_monitoring_service.* TO 'applifting'@'%';