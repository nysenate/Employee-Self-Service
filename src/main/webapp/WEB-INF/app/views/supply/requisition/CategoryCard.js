import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

const useQuery = () => {
    return new URLSearchParams(useLocation().search);
};

const CategoryCard = () => {
    const navigate = useNavigate();
    const query = useQuery();
    const [selectedCategories, setSelectedCategories] = useState(query.getAll('category') || []);

    const handleCategoryClick = (category) => {
        const newSelectedCategories = selectedCategories.includes(category)
            ? selectedCategories.filter(cat => cat !== category)
            : [...selectedCategories, category];

        setSelectedCategories(newSelectedCategories);
        navigate(`/requisition-form?${new URLSearchParams({ category: newSelectedCategories }).toString()}`);
    };

    return (
        <div>
            <h2>Categories</h2>
            <ul>
                {['Category 1', 'Category 2', 'Category 3'].map(category => (
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