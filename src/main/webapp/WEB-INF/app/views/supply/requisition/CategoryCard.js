import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, useSearchParams } from 'react-router-dom';

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

    return (
        <div>
            <h2>Categories</h2>
            <ul>
                {categories.map(category => (
                    <li key={category} onClick={() => handleCategoryClick(category)}>
                        <input
                            type="checkbox"
                            checked={selectedCategories.includes(category)}
                            readOnly
                        />
                        {category}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default CategoryCard;
