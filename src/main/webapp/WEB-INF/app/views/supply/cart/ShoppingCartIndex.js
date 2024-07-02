import React, { useState, useEffect } from 'react';
import styles from './ShoppingCartIndex.module.css';
import { Button } from "../../../components/Button";
import Hero from "../../../components/Hero";
import { OverOrderPopup, CheckOutPopup, PostCheckOutPopup, EmptyCartPopup } from "../../../components/Popups";
import { updateItemQuantity, clearCart } from '../cartUtils';
import { Link, useNavigate } from 'react-router-dom';
import { getItems } from "../helpers";

const Destination = () => {
  const locId = JSON.parse(localStorage.getItem('destination')).locId;
  const locationDescription = JSON.parse(localStorage.getItem('destination')).locationDescription;

  return (
    <div className={styles.subHeroContainer}>
      <div className={styles.subHeroContext}>
        <div className={styles.destinationContainer}>
          <span style={{fontWeight: '700', color: '#374282'}}>Destination: </span>{locId} ({locationDescription})
        </div>
      </div>
    </div>
  )
}

//Items
const ItemsGrid = ({ items, cart, handleQuantityChange, handleOverOrderAttempt }) => {
  return (
    <div className={styles.itemGrid}>
      {items.map((item) => (
        <ItemDisplay
          key={item.id}
          item={item}
          cart={cart}
          handleQuantityChange={handleQuantityChange}
          handleOverOrderAttempt={handleOverOrderAttempt}
        />
      ))}
    </div>
  );
};

// Item display component for each cart item
const ItemDisplay = ({ item, cart, handleQuantityChange, handleOverOrderAttempt }) => {
  const itemInCart = cart[item.id];
  const isMaxQuantity = itemInCart && itemInCart >= item.perOrderAllowance;
  const [localValue, setLocalValue] = useState(cart[item.id] || 0);

  // Synchronize localValue and itemInCart
  useEffect(() => {
    setLocalValue(itemInCart || 0);
  }, [itemInCart]);

  const handleTempInputChange = (e) => {
    const { value } = e.target;
    if (/^\d*$/.test(value)) { // Only allow numbers
      setLocalValue(value);
    }
  };

  if (!itemInCart) return null;

  return (
    <div className={styles.itemCard}>
      <div className={styles.itemImageContainer}>
        <div className={styles.itemImageContent}>
          <img
            className={styles.supplyItemImage}
            src={`/assets/supply_photos/${item.commodityCode}.jpg`}
            alt={item.description}
            height="120"
          />
        </div>
      </div>
      <div className={styles.titleContainer}>
        <div className={styles.titleContent}>
          <h3>{item.description}</h3>
        </div>
      </div>
      <div className={styles.qtyContainer}>
        <div className={styles.qtySelectorContainer}>
          <div className="text-align-center" style={{textAlign: 'center', width: '200px'}}>
            <p style={{color: 'dark-grey', margin: '0', height: '18px'}}>{item.unit}</p>
            <div  className={styles.qtySelector}>
              {/* Decrement Button */}
              <button
                className={styles.qtyAdjustButton}
                onClick={() => handleQuantityChange(item.id, Math.max(0, parseInt(localValue, 10) - 1))}
              >
                -
              </button>

              {/* Quantity Input */}
              <input
                className={styles.qtyInput}
                style={{ color: parseInt(localValue, 10) > item.perOrderAllowance ? 'red' : '' }}
                type="text"
                value={localValue}
                onChange={handleTempInputChange}
                onBlur={() => {
                  const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                  if (numericLocalValue > item.perOrderAllowance && itemInCart <= item.perOrderAllowance) {
                    handleOverOrderAttempt(item.id, numericLocalValue);
                    setLocalValue(cart[item.id]);
                  } else {
                    handleQuantityChange(item.id, numericLocalValue);
                  }
                }}
              />

              {/* Increment Button */}
              <button
                className={styles.qtyAdjustButton}
                onClick={() => {
                  const numericLocalValue = parseInt(localValue, 10); // Ensure it's a number
                  if (numericLocalValue === item.perOrderAllowance) {
                    handleOverOrderAttempt(item.id, numericLocalValue + 1);
                  } else {
                    handleQuantityChange(item.id, numericLocalValue + 1);
                  }
                }}
                style={{ backgroundColor: isMaxQuantity ? 'red' : '' }}
              >
                +
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
//End Items

const SpecialInstructions = ({ openEmptyCartPopup, openCheckOutPopup }) => {
  const [instructions, setInstructions] = useState('');

  return (
    <div className={styles.specialInstructionsContainer}>
      <label className={styles.specialInstructionsLabel} htmlFor="special-instructions-area">Special Instructions</label>
      <textarea
        id="special-instructions-area"
        className={styles.specialInstructionsTextArea}
        value={instructions}
        onChange={(e) => setInstructions(e.target.value)}
      />
      <div className={styles.buttonGroup}>
        <Button style={{ backgroundColor: "#8d8d8d", margin: "5px" }} onClick={openEmptyCartPopup}>Empty Cart</Button>
        <Link to="/supply/shopping/order" style={{ textDecoration: 'none' }}>
          <Button style={{ marginLeft: '5px', backgroundColor: "#8d8d8d", margin: "5px" }}>
            Continue Browsing
          </Button>
        </Link>
        <Button style={{ marginLeft: '5px', margin: "5px" }} onClick={openCheckOutPopup}>Checkout</Button>
      </div>
      <div className={styles.clearfix}></div>
    </div>
  );
};


export default function ShoppingCart() {
  const navigate = useNavigate()
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  const [items, setItems] = useState([]);
  useEffect(() => {
    //Clean up pending if refresh occured before popup conclusion
    localStorage.removeItem('pending');
    localStorage.removeItem('pendingQuantity');
    //Get Destination
    let destination = JSON.parse(localStorage.getItem('destination'));
    if (destination) {
      const fetchItems = async () => {
        const fetchedItems = await getItems(destination.locId);
        setItems(fetchedItems);
      };
      fetchItems();
    }
  }, []);
  const handleOverOrderAttempt = (itemId, newQuantity) => {
    localStorage.setItem('pending', JSON.stringify(itemId));
    localStorage.setItem('pendingQuantity', JSON.stringify(newQuantity));
    openOverOrderPopup();
  }
  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleClearCart = () => {
    clearCart();
    setCart({});
  };

  //POPUPS START:
  const [isOverOrderPopupOpen, setIsOverOrderPopupOpen] = useState(false);
  const [isCheckOutPopupOpen, setIsCheckOutPopupOpen] = useState(false);
  const [isPostCheckOutPopupOpen, setIsPostCheckOutPopupOpen] = useState(false);
  const [isEmptyCartPopupOpen, setEmptyCartPopupOpen] = useState(false);

  const openOverOrderPopup = () => {
    setIsOverOrderPopupOpen(true);
  };
  const closeOverOrderPopup = () => {
    setIsOverOrderPopupOpen(false);
  };
  const handleOverOrderAction = (decision) => {
    if(decision) {
      //retrieve pending:
      let pending = JSON.parse(localStorage.getItem('pending'));
      let pendingQuantity = JSON.parse(localStorage.getItem('pendingQuantity'));
      handleQuantityChange(pending, pendingQuantity);
    }
    localStorage.removeItem('pending');
    localStorage.removeItem('pendingQuantity');
  }
  const openCheckOutPopup = () => {
    setIsCheckOutPopupOpen(true);
  };
  const closeCheckOutPopup = () => {
    setIsCheckOutPopupOpen(false);
  };
  // NEEDS IMPLEMENTING WITH PAYLOAD
  // Payload: customerId, deliveryMethod (decision), destinationId (-W), lineItems ([{},{},...]), specialInstructions ("")
  //see photo for lineItems specifics
  const handleCheckOutAction = (decision) => {
    if(decision === 'PICKUP' || decision === 'DELIVERY'){
      console.log("implement handleCheckOutAction('",decision,"')");
      console.log("implement handleCheckOutAction('DELIVERY')");
      openPostCheckOutPopup();
    }else{
      console.error("Unexpected return: ", decision, ", should be either PICKUP or DELIVERY.")
    }
  }
  const openPostCheckOutPopup = () => {
    setIsPostCheckOutPopupOpen(true);
  };
  const closePostCheckOutPopup = () => {
    setIsPostCheckOutPopupOpen(false);
  };
  const handlePostCheckOutAction = (decision) => {
    if(decision === 'logout'){
      navigate("/logout");
    }else if(decision === 'return'){
      navigate("/supply/shopping/order");
    }else{
      console.error("Unexpected return: ", decision, ", should be either logout or return.")
    }
  }
  const openEmptyCartPopup = () => {
    setEmptyCartPopupOpen(true);
  };
  const closeEmptyCartPopup = () => {
    setEmptyCartPopupOpen(false);
  };
  const handleEmptyCartAction = (decision) => {
    if(decision) { handleClearCart(); }
  }

  return (
    <div>
      <Hero>Shopping Cart</Hero>
      <div className="content-container content-controls">
        {Object.keys(cart).length === 0 ? (
          <div>
            <div className={styles.emptyCartMessage}>Your cart is empty.</div>
            <div className={styles.emptyCartContainer}>
              <Link to="/supply/shopping/order" style={{ textDecoration: 'none' }}>
                <Button style={{ backgroundColor: "grey" }}>Continue Browsing</Button>
              </Link>
            </div>
          </div>
        ) : (
           <>
             <Destination />
             <ItemsGrid
               items={items}
               cart={cart}
               handleQuantityChange={handleQuantityChange}
               handleOverOrderAttempt={handleOverOrderAttempt}
             />
             <div className={styles.cartCheckoutContainer}>
               <SpecialInstructions openEmptyCartPopup={openEmptyCartPopup} openCheckOutPopup={openCheckOutPopup}/>
             </div>
           </>
         )}
      </div>
      <OverOrderPopup
        isModalOpen={isOverOrderPopupOpen}
        closeModal={closeOverOrderPopup}
        onAction={handleOverOrderAction}
      />
      <CheckOutPopup
        isModalOpen={isCheckOutPopupOpen}
        closeModal={closeCheckOutPopup}
        onAction={handleCheckOutAction}
      />
      <PostCheckOutPopup
          isModalOpen={isPostCheckOutPopupOpen}
          closeModal={closePostCheckOutPopup}
          onAction={handlePostCheckOutAction}
      />
      <EmptyCartPopup
        isModalOpen={isEmptyCartPopupOpen}
        closeModal={closeEmptyCartPopup}
        onAction={handleEmptyCartAction}
      />
    </div>
  );
}
