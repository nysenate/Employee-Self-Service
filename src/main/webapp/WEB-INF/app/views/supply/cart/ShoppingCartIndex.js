import React, { useState, useEffect } from 'react';
import styles from './ShoppingCartIndex.module.css';
import { Button } from "../../../components/Button";
import Hero from "../../../components/Hero";
import { OverOrderPopup, CheckOutPopup, EmptyCartPopup } from "../../../components/Popups";
import { incrementItem, decrementItem, clearCart } from '../cartUtils';
import { Link } from 'react-router-dom';
import { fetchApiJson } from "app/utils/fetchJson";

const Destination = () => {
  const locId = JSON.parse(localStorage.getItem('destination')).locId;
  const locationDescription = JSON.parse(localStorage.getItem('destination')).locationDescription;
  return (
    <div className="bg-white content-info" style={{ height: "70px", color: "black", borderBottom: 'none', marginBottom: '20px'}}>
      <div className="padding-10" style={{ display: 'flex'}}>
        <div style={{ display: 'inline-block'}}>
          <span className="supply-text">Destination: </span>{locId} ({locationDescription})
        </div>
      </div>
    </div>
  );
}

//Items
const ItemsGrid = ({ items, cart, handleIncrement, handleDecrement, handleOverOrderAttempt }) => {
  return (
    <div className={styles.itemGrid}>
      {items.map((item) => (
        <ItemDisplay
          key={item.id}
          item={item}
          cart={cart}
          handleIncrement={handleIncrement}
          handleDecrement={handleDecrement}
          handleOverOrderAttempt={handleOverOrderAttempt}
        />
      ))}
    </div>
  );
};

// Item display component for each cart item
const ItemDisplay = ({ item, cart, handleIncrement, handleDecrement, handleOverOrderAttempt }) => {
  const itemInCart = cart[item.id];
  const isMaxQuantity = itemInCart && itemInCart >= item.perOrderAllowance;

  if(!itemInCart) return;

  return (
    <div className={styles.itemCard}>
      <div className={styles.itemImage}>
        <img
          src={`/assets/supply_photos/${item.commodityCode}.jpg`}
          alt={item.description}
          height="120"
        />
      </div>
      <div className={styles.itemDescription}>
        <h4>{item.description}</h4>
        <p>{item.unit}</p>
      </div>
      {
        <div className={styles.cartControls}>
          <Button onClick={() => handleDecrement(item.id)}>-</Button>
          <span>{itemInCart}</span>
          <Button
            onClick={() => itemInCart===item.perOrderAllowance ? handleOverOrderAttempt(item.id) : handleIncrement(item.id)}
            style={{ backgroundColor: isMaxQuantity ? 'red' : '' }}
          >
            +
          </Button>
        </div>
      }
    </div>
  );
};
//End Items

const SpecialInstructions = ({ openEmptyCartPopup, openCheckOutPopupOpen }) => {
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
        <Button style={{ backgroundColor: "grey", margin: "5px" }} onClick={openEmptyCartPopup}>Empty Cart</Button>
        <Link to="/supply/shopping/order" style={{ textDecoration: 'none' }}>
          <Button style={{ marginLeft: '5px', backgroundColor: "grey", margin: "5px" }}>
            Continue Browsing
          </Button>
        </Link>
        <Button style={{ marginLeft: '5px', margin: "5px" }} onClick={openCheckOutPopupOpen}>Checkout</Button>
      </div>
      <div className={styles.clearfix}></div>
    </div>
  );
};

const getItems = async (locId) => {
  return await fetchApiJson(`/supply/items/orderable/${locId}`).then((body) => body.result);
};

export default function ShoppingCart() {
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  const [items, setItems] = useState([]);
  useEffect(() => {
    localStorage.removeItem('pending'); //Clean up pending if refresh occured before popup conclusion
    let destination = JSON.parse(localStorage.getItem('destination')).locId;
    if (destination) {
      const fetchItems = async () => {
        const fetchedItems = await getItems(destination);
        setItems(fetchedItems);
      };
      fetchItems();
    }
  }, []);
  const handleIncrement = (itemId) => {
    incrementItem(itemId);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleOverOrderAttempt = (itemId) => {
    console.log("handleOverOrderAttempt()::::");
    console.log("Attempting to over order item.id: ", itemId);
    console.log("Setting item.id: ", itemId, " as pending item")
    //setPendingItemId
    localStorage.setItem('pending', JSON.stringify(itemId));
    let pending = JSON.parse(localStorage.getItem('pending'));
    if(pending){
      console.log("item.id: ", itemId, " set as pending item: ", pending);
    }else{
      console.err("Could not set item.id: ", itemId, " as pending item: ", pending);
    }
    console.log("opening over order popup...");
    openOverOrderPopup();
  }
  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleDecrement = (itemId) => {
    decrementItem(itemId);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };
  const handleClearCart = () => {
    clearCart();
    setCart({});
  };


  //POPUPS START:
  const [isOverOrderPopupOpen, setIsOverOrderPopupOpen] = useState(false);
  const [isCheckOutPopupOpen, setIsCheckOutPopupOpen] = useState(false);
  const [isEmptyCartPopupOpen, setEmptyCartPopupOpen] = useState(false);

  const openOverOrderPopup = () => {
    setIsOverOrderPopupOpen(true);
  };
  const closeOverOrderPopup = () => {
    setIsOverOrderPopupOpen(false);
  };
  const handleOverOrderAction = (decision) => {
    console.log("handleOverOrderAction()::::");
    console.log("Over Order decision: ", decision);
    let pending ='unretrieved';
    if(decision) {
      //retrieve pending:
      pending = JSON.parse(localStorage.getItem('pending'));
      console.log("Retrieved pending item id: ", pending);
      console.log("Attempting to over order pending item: ", pending);
      handleIncrement(pending);
    }
    console.log("deleting pending item id: ", pending)
    //deletePendingItemId:
    localStorage.removeItem('pending');
    pending = JSON.parse(localStorage.getItem('pending'));
    console.log("Success?? -> Remaining pending item id: ", pending);
  }
  const openCheckOutPopupOpen = () => {
    setIsCheckOutPopupOpen(true);
  };
  const closeCheckOutPopupOpen = () => {
    setIsCheckOutPopupOpen(false);
  };
  const handleCheckOutAction = (decision) => {
    if(decision === 'pickup'){
      console.log("implement handleCheckOutAction('pickup')");
    }else if(decision === 'delivery'){
      console.log("implement handleCheckOutAction('delivery')");
    }else{
      console.error("Unexpected return: ", decision, ", should be either pickup or delivery.")
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
               handleIncrement={handleIncrement}
               handleDecrement={handleDecrement}
               handleOverOrderAttempt={handleOverOrderAttempt}
             />
             <div className={styles.cartCheckoutContainer}>
               <SpecialInstructions openEmptyCartPopup={openEmptyCartPopup} openCheckOutPopupOpen={openCheckOutPopupOpen}/>
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
        closeModal={closeCheckOutPopupOpen}
        onAction={handleCheckOutAction}
      />
      <EmptyCartPopup
        isModalOpen={isEmptyCartPopupOpen}
        closeModal={closeEmptyCartPopup}
        onAction={handleEmptyCartAction}
      />
    </div>
  );
}
