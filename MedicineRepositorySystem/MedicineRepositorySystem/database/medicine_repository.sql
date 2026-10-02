-- ======================================================================
-- Medicine Repository System - Database Setup Script
-- Database: medicine_repository
-- Description: Schema and 40 Initial Realistic Medicine Records + Default Users
-- ======================================================================

-- 1. Create database if not exists
CREATE DATABASE IF NOT EXISTS medicine_repository;
USE medicine_repository;

-- 2. Drop existing tables if recreating
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS users;

-- 3. Create Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Create Medicines Table
CREATE TABLE medicines (
    medicine_id VARCHAR(50) PRIMARY KEY,
    medicine_name VARCHAR(120) NOT NULL,
    generic_name VARCHAR(120) NOT NULL,
    category VARCHAR(60) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    dosage_form VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    uses TEXT NOT NULL,
    prescription_required TINYINT(1) NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock_quantity INT NOT NULL CHECK (stock_quantity >= 0),
    manufacturing_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    storage_instructions VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_dates CHECK (expiry_date >= manufacturing_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Insert Default Accounts (1 ADMIN, 1 USER)
INSERT INTO users (name, email, phone, username, password, role) VALUES
('System Administrator', 'admin@medrepo.org', '+1-555-0100', 'admin', 'admin123', 'ADMIN'),
('Dr. Jane Miller', 'jane.miller@hospital.org', '+1-555-0101', 'user', 'user123', 'USER');

-- 6. Insert Exactly 40 Realistic Medicine Records
-- Covering 10 distinct categories:
-- Pain Relief, Antibiotics, Antihistamines, Antacids, Diabetes, Blood Pressure, Vitamins, Respiratory, Gastrointestinal, Cardiovascular
INSERT INTO medicines (
    medicine_id, medicine_name, generic_name, category, manufacturer, dosage, dosage_form,
    description, uses, prescription_required, price, stock_quantity,
    manufacturing_date, expiry_date, batch_number, storage_instructions
) VALUES
-- 1. Pain Relief
('MED-1001', 'Tylenol Extra Strength', 'Paracetamol', 'Pain Relief', 'Johnson & Johnson', '500 mg', 'Tablet',
 'Fast-acting analgesic and antipyretic medication designed for temporary relief of mild to moderate aches and fevers.',
 'Headaches, muscular aches, toothaches, common cold fevers, backaches', 0, 8.50, 150,
 '2025-01-15', '2027-06-30', 'BN-TYL-2025-01', 'Store between 20°C to 25°C. Protect from moisture.'),

-- 2. Pain Relief
('MED-1002', 'Advil Liqui-Gels', 'Ibuprofen', 'Pain Relief', 'Pfizer Consumer Healthcare', '200 mg', 'Capsule',
 'Nonsteroidal anti-inflammatory drug (NSAID) engineered for swift relief of systemic inflammation, swelling, and localized pain.',
 'Arthritis flare-ups, joint pain, menstrual cramps, toothache, muscle soreness', 0, 11.25, 85,
 '2025-03-10', '2027-08-15', 'BN-ADV-8841', 'Store at room temperature 15°C to 30°C. Avoid excessive heat.'),

-- 3. Pain Relief
('MED-1003', 'Aleve All-Day Strong', 'Naproxen Sodium', 'Pain Relief', 'Bayer Healthcare', '220 mg', 'Tablet',
 'Extended-release NSAID providing sustained 12-hour relief from inflammatory pain conditions.',
 'Osteoarthritis, rheumatoid symptoms, chronic stiffness, tendinitis', 0, 13.99, 4,
 '2024-11-01', '2026-11-15', 'BN-ALE-4402', 'Store in dry place below 25°C. Keep bottle tightly closed.'),

-- 4. Pain Relief (Out of stock)
('MED-1004', 'Voltaren Emulgel', 'Diclofenac Diethylamine', 'Pain Relief', 'GSK Consumer Health', '1.16% w/w', 'Gel',
 'Targeted topical NSAID gel formulated for direct absorption through skin into inflamed joints.',
 'Localized joint osteoarthritis, acute sprains, soft tissue trauma', 0, 16.50, 0,
 '2025-02-01', '2027-04-30', 'BN-VOL-9011', 'Store below 30°C. Do not freeze.'),

-- 5. Antibiotics
('MED-1005', 'Amoxil Oral Suspension', 'Amoxicillin', 'Antibiotics', 'GlaxoSmithKline', '250 mg/5ml', 'Oral Suspension',
 'Broad-spectrum beta-lactam penicillin class antibiotic that inhibits bacterial cell wall synthesis.',
 'Otitis media, acute sinusitis, streptococcal pharyngitis, lower respiratory tract infections', 1, 14.80, 42,
 '2025-05-12', '2027-05-10', 'BN-AMX-7731', 'Reconstituted suspension store in refrigerator 2°C to 8°C. Discard after 14 days.'),

-- 6. Antibiotics
('MED-1006', 'Zithromax Z-Pak', 'Azithromycin', 'Antibiotics', 'Pfizer Inc.', '250 mg', 'Tablet',
 'Macrolide antimicrobial agent active against Gram-positive and atypical respiratory pathogens.',
 'Community-acquired pneumonia, acute bacterial exacerbations of COPD, chlamydial urethritis', 1, 32.00, 60,
 '2025-04-01', '2027-09-30', 'BN-ZTH-1022', 'Store at room temperature 15°C to 30°C.'),

-- 7. Antibiotics (Low stock)
('MED-1007', 'Cipro XR', 'Ciprofloxacin', 'Antibiotics', 'Bayer Pharma', '500 mg', 'Tablet',
 'Second-generation fluoroquinolone with potent bactericidal activity against aerobic Gram-negative bacilli.',
 'Complicated urinary tract infections, infectious diarrhea, typhoid fever, bone infections', 1, 24.50, 7,
 '2024-08-10', '2026-10-25', 'BN-CPR-3310', 'Store at controlled room temperature 20°C to 25°C away from light.'),

-- 8. Antibiotics
('MED-1008', 'Augmentin Duo', 'Amoxicillin + Clavulanic Acid', 'Antibiotics', 'GlaxoSmithKline', '625 mg', 'Tablet',
 'Synergistic combination of broad-spectrum amoxicillin and beta-lactamase inhibitor clavulanate.',
 'Recurrent tonsillitis, dental abscesses, polymicrobial skin infections, urinary tract sepsis', 1, 28.75, 95,
 '2025-06-20', '2027-12-15', 'BN-AUG-9912', 'Store in original moisture-proof blister foil below 25°C.'),

-- 9. Antihistamines
('MED-1009', 'Zyrtec 24 Hour Allergy', 'Cetirizine Hydrochloride', 'Antihistamines', 'Johnson & Johnson', '10 mg', 'Tablet',
 'Second-generation piperazine H1-receptor antagonist providing non-drowsy allergy relief.',
 'Seasonal allergic rhinitis, perennial allergic rhinitis, chronic idiopathic urticaria', 0, 18.20, 110,
 '2025-01-20', '2028-01-15', 'BN-ZYR-0419', 'Store between 20°C and 25°C.'),

-- 10. Antihistamines
('MED-1010', 'Claritin Non-Drowsy', 'Loratadine', 'Antihistamines', 'Bayer Healthcare', '10 mg', 'Tablet',
 'Selective peripheral histamine H1 receptor blocker with minimal central nervous system penetration.',
 'Allergic conjunctivitis, sneezing, itchy runny nose, allergic hives', 0, 17.50, 75,
 '2025-03-05', '2027-10-20', 'BN-CLR-5110', 'Store in dry place between 15°C and 30°C.'),

-- 11. Antihistamines (Low stock)
('MED-1011', 'Allegra Allergy 12HR', 'Fexofenadine Hydrochloride', 'Antihistamines', 'Sanofi-Aventis', '60 mg', 'Tablet',
 'Third-generation antihistamine with zero sedating side effects across therapeutic doses.',
 'Hay fever symptoms, skin itching, seasonal pollen hypersensitivity', 0, 15.90, 6,
 '2024-09-15', '2026-11-30', 'BN-ALG-6003', 'Store between 20°C to 25°C. Protect from excessive light.'),

-- 12. Antihistamines (Expired)
('MED-1012', 'Benadryl Allergy Rapid', 'Diphenhydramine HCl', 'Antihistamines', 'Kenvue Inc.', '25 mg', 'Capsule',
 'First-generation sedating ethanolamine H1 antagonist with mild antimuscarinic action.',
 'Acute anaphylactoid allergic manifestations, motion sickness, nighttime allergy relief', 0, 7.80, 18,
 '2023-01-10', '2025-02-15', 'BN-BEN-2201', 'Store between 15°C and 30°C. Protect from moisture.'),

-- 13. Antacids
('MED-1013', 'Prilosec OTC', 'Omeprazole', 'Antacids', 'Procter & Gamble', '20 mg', 'Delayed-Release Capsule',
 'Substituted benzimidazole proton pump inhibitor (PPI) suppressing gastric acid secretion.',
 'Frequent heartburn occurring 2 or more days a week, acid reflux, erosive esophagitis', 0, 22.40, 88,
 '2025-02-15', '2027-08-30', 'BN-PRL-8910', 'Store at controlled room temperature 20°C to 25°C.'),

-- 14. Antacids
('MED-1014', 'Protonix Gastro', 'Pantoprazole Sodium', 'Antacids', 'Wyeth / Pfizer', '40 mg', 'Enteric Coated Tablet',
 'Potent gastric acid secretion suppressor through irreversible H+/K+ ATPase inhibition.',
 'GERD, Zollinger-Ellison syndrome, peptic and duodenal ulcer healing', 1, 19.80, 64,
 '2025-04-10', '2027-11-20', 'BN-PAN-3312', 'Keep blister dry. Store at 20°C to 25°C.'),

-- 15. Antacids
('MED-1015', 'Nexium 24HR', 'Esomeprazole Magnesium', 'Antacids', 'AstraZeneca', '20 mg', 'Capsule',
 'S-enantiomer of omeprazole with higher bioavailability for superior acid suppression.',
 'Persistent gastric reflux, stomach lining protection during NSAID therapy', 0, 25.10, 52,
 '2025-05-18', '2028-02-10', 'BN-NEX-0021', 'Store at room temperature 15°C to 30°C.'),

-- 16. Antacids (Out of stock)
('MED-1016', 'Tums Ultra 1000', 'Calcium Carbonate', 'Antacids', 'GSK Consumer', '1000 mg', 'Chewable Tablet',
 'Fast-acting chemical neutralizer that reacts immediately with hydrochloric acid in stomach.',
 'Occasional acid indigestion, upset stomach, sour stomach, rapid heartburn comfort', 0, 6.20, 0,
 '2025-03-01', '2028-05-15', 'BN-TUM-4451', 'Store in dry place below 25°C. Avoid humidity.'),

-- 17. Diabetes
('MED-1017', 'Glucophage XR', 'Metformin Hydrochloride', 'Diabetes', 'Merck KGaA', '500 mg', 'Extended-Release Tablet',
 'Biguanide antihyperglycemic agent reducing hepatic gluconeogenesis and enhancing insulin sensitivity.',
 'First-line management of Type 2 diabetes mellitus, insulin resistance syndrome', 1, 16.00, 140,
 '2025-01-25', '2028-04-30', 'BN-GLU-9022', 'Store at 20°C to 25°C. Excursions permitted to 15°C to 30°C.'),

-- 18. Diabetes
('MED-1018', 'Januvia Oral', 'Sitagliptin Phosphate', 'Diabetes', 'Merck Sharp & Dohme', '100 mg', 'Tablet',
 'Dipeptidyl peptidase-4 (DPP-4) inhibitor that boosts active incretin hormone concentrations.',
 'Glycemic control in Type 2 diabetes alongside diet and exercise modifications', 1, 85.50, 40,
 '2025-06-01', '2027-10-31', 'BN-JAN-1190', 'Store at room temperature 20°C to 25°C.'),

-- 19. Diabetes (Low stock)
('MED-1019', 'Amaryl Sulfonylurea', 'Glimepiride', 'Diabetes', 'Sanofi-Aventis', '2 mg', 'Tablet',
 'Long-acting second-generation sulfonylurea stimulating insulin release from pancreatic beta cells.',
 'Type 2 diabetes mellitus inadequate glycemic response to metformin monotherapy', 1, 12.30, 8,
 '2024-10-15', '2026-11-20', 'BN-AMY-6721', 'Store below 25°C in original package.'),

-- 20. Diabetes
('MED-1020', 'Jardiance Glycemic', 'Empagliflozin', 'Diabetes', 'Boehringer Ingelheim', '10 mg', 'Film-Coated Tablet',
 'Selective sodium-glucose co-transporter 2 (SGLT2) inhibitor promoting urinary glucose excretion.',
 'Type 2 diabetes glycemic control and reduction of cardiovascular death risk in heart failure', 1, 98.00, 35,
 '2025-03-20', '2028-06-30', 'BN-JAR-5520', 'Store at room temperature 25°C.'),

-- 21. Blood Pressure
('MED-1021', 'Norvasc Vascular', 'Amlodipine Besylate', 'Blood Pressure', 'Pfizer Inc.', '5 mg', 'Tablet',
 'Long-acting dihydropyridine calcium channel blocker inhibiting calcium ion influx into vascular smooth muscle.',
 'Essential hypertension, chronic stable angina pectoris, vasospastic angina', 1, 15.20, 130,
 '2025-02-10', '2028-01-20', 'BN-NOR-1082', 'Store between 15°C and 30°C. Protect from light.'),

-- 22. Blood Pressure
('MED-1022', 'Cozaar Cardio', 'Losartan Potassium', 'Blood Pressure', 'Merck & Co.', '50 mg', 'Tablet',
 'Potent synthetic angiotensin II receptor type AT1 antagonist reducing systemic vascular resistance.',
 'Hypertension management, nephropathy reduction in Type 2 diabetic hypertensive patients', 1, 18.40, 92,
 '2025-04-12', '2027-12-15', 'BN-COZ-3329', 'Store at controlled room temperature 20°C to 25°C.'),

-- 23. Blood Pressure
('MED-1023', 'Toprol XL Chrono', 'Metoprolol Succinate', 'Blood Pressure', 'AstraZeneca', '50 mg', 'Extended-Release Tablet',
 'Cardioselective beta-1 adrenergic receptor blocker stabilizing cardiac rhythm and decreasing cardiac workload.',
 'Hypertension, post-myocardial infarction secondary prophylaxis, symptomatic heart failure', 1, 21.60, 50,
 '2025-01-08', '2027-09-15', 'BN-TOP-7182', 'Store at room temperature 20°C to 25°C.'),

-- 24. Blood Pressure (Expired)
('MED-1024', 'Vasotec ACE', 'Enalapril Maleate', 'Blood Pressure', 'Bausch Health', '10 mg', 'Tablet',
 'Ester prodrug of enalaprilat that competitively blocks angiotensin converting enzyme.',
 'Hypertension control, left ventricular dysfunction management, heart failure progression slowdown', 1, 14.10, 12,
 '2023-03-01', '2025-04-15', 'BN-VAS-0922', 'Store at room temperature below 25°C. Keep desiccant in bottle.'),

-- 25. Vitamins
('MED-1025', 'Nature Made Vitamin D3', 'Cholecalciferol', 'Vitamins', 'Nature Made Nutritional', '2000 IU (50 mcg)', 'Softgel',
 'Essential fat-soluble secosteroid vitamin crucial for calcium absorption and skeletal homeostasis.',
 'Hypovitaminosis D prophylaxis, osteoporosis bone density maintenance, immune defense support', 0, 12.50, 160,
 '2025-03-01', '2028-03-31', 'BN-VIT-3011', 'Store tightly capped in a cool dry place below 25°C.'),

-- 26. Vitamins
('MED-1026', 'Caltrate 600+D3 Plus', 'Calcium Carbonate + Cholecalciferol', 'Vitamins', 'Haleon Consumer Health', '600 mg / 800 IU', 'Tablet',
 'Concentrated bioavailable calcium formulation compounded with vitamin D3 and essential bone minerals.',
 'Calcium deficiency management, osteoporosis prevention in postmenopausal women', 0, 16.75, 78,
 '2025-04-15', '2027-11-30', 'BN-CAL-6621', 'Store at 20°C to 25°C. Protect from humidity.'),

-- 27. Vitamins
('MED-1027', 'Neurobion Forte', 'Thiamine + Pyridoxine + Cyanocobalamin', 'Vitamins', 'Procter & Gamble Health', '100mg/200mg/200mcg', 'Tablet',
 'High-potency neurotropic B-complex vitamin formula rejuvenating damaged peripheral nerve sheaths.',
 'Peripheral neuropathy, diabetic nerve pain, paresthesias, neuralgic back pain', 0, 9.90, 85,
 '2025-02-28', '2027-10-15', 'BN-NEU-5544', 'Store below 25°C. Protect from direct sunlight.'),

-- 28. Vitamins (Low stock)
('MED-1028', 'Ferro-Sequels Iron', 'Ferrous Fumarate + Docusate', 'Vitamins', 'Meda Pharmaceuticals', '65 mg Elemental Iron', 'Tablet',
 'Timed-release therapeutic iron supplement gentle on gastrointestinal mucosa.',
 'Iron deficiency anemia, prenatal dietary iron supplementation, blood loss recovery', 0, 14.20, 5,
 '2024-11-20', '2026-10-28', 'BN-FER-8109', 'Store at controlled room temperature 15°C to 30°C.'),

-- 29. Respiratory
('MED-1029', 'Singulair Pediatric & Adult', 'Montelukast Sodium', 'Respiratory', 'Organon & Co.', '10 mg', 'Film-Coated Tablet',
 'Selective cysteinyl leukotriene receptor antagonist inhibiting bronchoconstrictive inflammatory cascades.',
 'Prophylaxis and chronic treatment of bronchial asthma, exercise-induced bronchospasm', 1, 34.00, 68,
 '2025-05-10', '2028-02-28', 'BN-SNG-2091', 'Store at 20°C to 25°C in moisture-resistant original container.'),

-- 30. Respiratory
('MED-1030', 'Ventolin HFA Inhaler', 'Albuterol Sulfate', 'Respiratory', 'GlaxoSmithKline', '90 mcg/actuation', 'Inhaler',
 'Rapid-acting selective beta-2 adrenergic agonist providing immediate bronchodilation.',
 'Acute bronchospasm rescue in asthma, chronic bronchitis, and obstructive lung disease', 1, 48.00, 45,
 '2025-06-15', '2027-08-31', 'BN-VNT-9102', 'Store between 15°C and 25°C. Do not puncture or incinerate canister.'),

-- 31. Respiratory
('MED-1031', 'Symbicort Turbuhaler', 'Budesonide + Formoterol', 'Respiratory', 'AstraZeneca', '160 mcg / 4.5 mcg', 'Dry Powder Inhaler',
 'Dual maintenance inhaler pairing an inhaled corticosteroid with a long-acting beta2-agonist.',
 'Moderate to severe persistent asthma maintenance, maintenance therapy for COPD', 1, 72.50, 30,
 '2025-01-20', '2027-07-31', 'BN-SYM-4011', 'Store at room temperature with cap securely tightened.'),

-- 32. Respiratory (Out of stock)
('MED-1032', 'Flonase Allergy Relief', 'Fluticasone Propionate', 'Respiratory', 'Haleon Healthcare', '50 mcg/spray', 'Nasal Spray',
 'Glucocorticoid nasal suspension counteracting 6 key inflammatory nasal pathway substances.',
 'Nasal congestion, sinus pressure, allergic sneezing, watery eyes from airborne allergens', 0, 21.00, 0,
 '2025-02-14', '2027-06-30', 'BN-FLO-8823', 'Store between 4°C and 30°C. Shake gently before use.'),

-- 33. Gastrointestinal
('MED-1033', 'Imodium A-D Capsule', 'Loperamide Hydrochloride', 'Gastrointestinal', 'Johnson & Johnson', '2 mg', 'Capsule',
 'Synthetic opioid receptor agonist that slows intestinal motility and increases fluid resorption.',
 'Acute non-specific diarrhea, traveler diarrhea, management of chronic diarrhea in IBS', 0, 8.90, 115,
 '2025-03-25', '2028-01-15', 'BN-IMO-3301', 'Store at 20°C to 25°C. Avoid excessive moisture.'),

-- 34. Gastrointestinal
('MED-1034', 'Zofran ODT Rapid', 'Ondansetron', 'Gastrointestinal', 'Novartis Pharmaceuticals', '4 mg', 'Orally Disintegrating Tablet',
 'Potent, highly selective 5-HT3 receptor antagonist suppressing emetic reflex pathways.',
 'Prevention of nausea and vomiting induced by emetogenic chemotherapy or post-operative states', 1, 38.00, 38,
 '2025-04-18', '2027-10-31', 'BN-ZOF-7721', 'Store between 2°C and 30°C. Protect from light and moisture.'),

-- 35. Gastrointestinal
('MED-1035', 'MiraLAX Powder', 'Polyethylene Glycol 3350', 'Gastrointestinal', 'Bayer Healthcare', '17 g/dose', 'Oral Powder',
 'Osmotic laxative drawing water into the lumen to gently soften stool and facilitate bowel movement.',
 'Occasional constipation, bowel evacuation, gentle restoration of digestive transit', 0, 17.25, 62,
 '2025-01-12', '2028-05-30', 'BN-MIR-1920', 'Store at 20°C to 25°C.'),

-- 36. Gastrointestinal (Low stock)
('MED-1036', 'Buscopan Forte', 'Hyoscine Butylbromide', 'Gastrointestinal', 'Sanofi-Aventis', '20 mg', 'Tablet',
 'Peripherally acting antispasmodic targeting smooth muscle of the abdominal viscera.',
 'Abdominal spasms, painful cramping, colicky pain associated with irritable bowel syndrome', 0, 11.50, 9,
 '2024-12-05', '2026-11-10', 'BN-BUS-5021', 'Store dry at room temperature below 25°C.'),

-- 37. Cardiovascular
('MED-1037', 'Lipitor Cardioprotect', 'Atorvastatin Calcium', 'Cardiovascular', 'Viatris / Pfizer', '20 mg', 'Tablet',
 'Competitive HMG-CoA reductase inhibitor significantly lowering low-density lipoprotein cholesterol (LDL-C).',
 'Hypercholesterolemia, dyslipidemia, prevention of atherosclerotic cardiovascular events', 1, 26.50, 120,
 '2025-03-15', '2028-04-30', 'BN-LIP-6612', 'Store at controlled room temperature 20°C to 25°C.'),

-- 38. Cardiovascular
('MED-1038', 'Plavix Antithrombotic', 'Clopidogrel Bisulfate', 'Cardiovascular', 'Sanofi / Bristol Myers Squibb', '75 mg', 'Tablet',
 'Thienopyridine class P2Y12 platelet adenosine diphosphate receptor inhibitor preventing aggregation.',
 'Acute coronary syndrome, recent stroke or myocardial infarction prevention, peripheral arterial disease', 1, 31.00, 54,
 '2025-04-22', '2027-11-15', 'BN-PLV-4410', 'Store at 25°C. Excursions permitted to 15°C to 30°C.'),

-- 39. Cardiovascular
('MED-1039', 'Eliquis Anticoagulant', 'Apixaban', 'Cardiovascular', 'Bristol-Myers Squibb', '5 mg', 'Tablet',
 'Direct, selective, reversible inhibitor of coagulation factor Xa without requiring antithrombin III.',
 'Reduction of stroke and systemic embolism risk in non-valvular atrial fibrillation, DVT treatment', 1, 115.00, 22,
 '2025-05-05', '2027-09-30', 'BN-ELI-8834', 'Store at 20°C to 25°C. Protect from moisture.'),

-- 40. Cardiovascular (Expired)
('MED-1040', 'Nitrostat Sublingual', 'Nitroglycerin', 'Cardiovascular', 'Pfizer Inc.', '0.4 mg', 'Sublingual Tablet',
 'Organic nitrate converting into nitric oxide causing peripheral venous and coronary artery vasodilation.',
 'Acute relief of an attack or prophylaxis of angina pectoris due to coronary artery disease', 1, 19.50, 14,
 '2023-05-10', '2025-06-30', 'BN-NIT-1002', 'Store in original glass vial tightly closed below 25°C. Do not expose to heat.')
ON DUPLICATE KEY UPDATE medicine_name=VALUES(medicine_name);

-- ======================================================================
-- Verification Query
-- ======================================================================
SELECT COUNT(*) AS total_medicines,
       COUNT(DISTINCT category) AS total_categories,
       SUM(CASE WHEN stock_quantity = 0 THEN 1 ELSE 0 END) AS out_of_stock,
       SUM(CASE WHEN stock_quantity > 0 AND stock_quantity < 10 THEN 1 ELSE 0 END) AS low_stock,
       SUM(CASE WHEN stock_quantity >= 10 THEN 1 ELSE 0 END) AS available,
       SUM(CASE WHEN expiry_date < CURRENT_DATE THEN 1 ELSE 0 END) AS expired
FROM medicines;
