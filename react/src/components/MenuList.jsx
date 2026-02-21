import React from 'react';

const normalizeDishType = (type, dish = null) => {
  const fallbackRaw =
    type ??
    dish?.type ??
    dish?.dishType ??
    dish?.vegNonVeg ??
    (dish?.isVeg === true ? "veg" : dish?.isVeg === false ? "non-veg" : "");
  const value = String(fallbackRaw || "").toLowerCase().replace(/[^a-z]/g, "");
  if (!value) return "non-veg";
  if (value === "veg" || value === "vegetarian" || value === "vegan" || value.startsWith("veg")) return "veg";
  if (value === "nonveg" || value === "nonvegetarian" || value.includes("nonveg")) return "non-veg";
  return "non-veg";
};

const MenuList = ({ items, onAddToCart, onOpenDishDetails }) => {
  if (!items || items.length === 0) {
    return (
      <div className="no-items" style={{ textAlign: 'center', padding: '20px' }}>
        <h3>No dishes found.</h3>
      </div>
    );
  }

  return (
    <div className="menu-list">
      {items.map((dish) => {
        const normalizedType = normalizeDishType(dish.type, dish);

        return (
          <div key={dish.id || dish.name} className="dish-card">
            <div className="dish-media-container">
              <button
                type="button"
                className="dish-image-btn"
                onClick={() => onOpenDishDetails?.(dish)}
              >
                <img 
                  src={dish.image || dish.imageUrl}
                  alt={dish.name} 
                  className="dish-image"
                />
              </button>
            </div>

            <div className="dish-meta">
              <span className={`type-pill ${normalizedType || "non-veg"}`}>
                {normalizedType === 'veg' ? 'Veg' : 'Non-Veg'}
              </span>
              {dish.category && <span className="category-pill">{dish.category}</span>}
            </div>

            <h3>
              <button
                type="button"
                className="dish-name-btn"
                onClick={() => onOpenDishDetails?.(dish)}
              >
                {dish.name}
              </button>
            </h3>
            <p>{dish.description || "Freshly prepared and served hot."}</p>

            <div className="dish-footer">
              <strong>${Number(dish.price || 0).toFixed(2)}</strong>
              <button onClick={() => onAddToCart(dish)} className="add-btn">
                Add to Cart
              </button>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default MenuList;
