import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import styles from "../universalStyles.module.css";

const CategoryCard = ({ categories }) => {
    const [searchParams, setSearchParams] = useSearchParams();
    const [selectedCategories, setSelectedCategories] = useState(() => searchParams.getAll('category'));

    useEffect(() => {
        setSelectedCategories(searchParams.getAll('category'));
    }, [searchParams]);

    const handleCategoryClick = (category) => {
        const newSelectedCategories = selectedCategories.includes(category)
            ? selectedCategories.filter(cat => cat !== category)
            : [...selectedCategories, category];

        setSelectedCategories(newSelectedCategories);
        const newParams = new URLSearchParams();
        newSelectedCategories.forEach(cat => newParams.append('category', cat));
        setSearchParams(newParams);
    };

    const clearSections = () => {
        const newParams = new URLSearchParams();
        setSearchParams(newParams);
    }

    return (
        <div style={{display:'flex', flexDirection: 'column', marginBottom: '100px', height: '60vh', backgroundColor: 'white'}}>
            <div className={`${styles.flexHeader} ${styles.paddingX}`}>
                <a style={{ cursor: 'pointer', paddingLeft: '10px' }} onClick={clearSections}>Clear All</a>
            </div>
            <div className={styles.flexContent} style={{ overflowY: 'auto', maxHeight: '900px' }}>
                <ul>
                    {categories.map(category => (
                        <li key={category} onClick={() => handleCategoryClick(category)}>
                            <input
                                type="checkbox"
                                checked={selectedCategories.includes(category)}
                                readOnly
                            />
                            <label>{category}</label>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default CategoryCard;
