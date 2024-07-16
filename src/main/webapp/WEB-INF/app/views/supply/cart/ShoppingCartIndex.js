import React, { useState, useEffect } from 'react';
import styles from './ShoppingCartIndex.module.css';
import { Button } from "../../../components/Button";
import Hero from "../../../components/Hero";
import { OverOrderPopup, CheckOutPopup, PostCheckOutPopup, EmptyCartPopup } from "../../../components/Popups";
import { updateItemQuantity, clearCart } from '../cartUtils';
import { Link, useNavigate } from 'react-router-dom';
import { getItems } from "../helpers";
import Destination from "./Destination";
import ItemsGrid from "./ItemsGrid";
import SpecialInstructions from "./SpecialInstructions";
import { fetchApiJson } from "../../../utils/fetchJson";


export default function ShoppingCart() {
  const navigate = useNavigate()
  const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
  const [items, setItems] = useState([]);
  const [instructions, setInstructions] = useState('');
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
    setIsOverOrderPopupOpen(true);
  }
  const handleQuantityChange = (itemId, quantity) => {
    updateItemQuantity(itemId, quantity);
    setCart(JSON.parse(localStorage.getItem('cart')));
  };

  //POPUPS START:
  const [isOverOrderPopupOpen, setIsOverOrderPopupOpen] = useState(false);
  const [isCheckOutPopupOpen, setIsCheckOutPopupOpen] = useState(false);
  const [isPostCheckOutPopupOpen, setIsPostCheckOutPopupOpen] = useState(false);
  const [isEmptyCartPopupOpen, setEmptyCartPopupOpen] = useState(false);

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

  const handleCheckOutAction = (decision) => {
    if(decision === 'PICKUP' || decision === 'DELIVERY'){
      checkoutPost(decision);
      setIsPostCheckOutPopupOpen(true);
    }else{
      console.error("Unexpected return: ", decision, ", should be either PICKUP or DELIVERY.")
    }
  }
  const closePostCheckOutPopup = () => {
    setIsPostCheckOutPopupOpen(false);
  };
  const handlePostCheckOutAction = (decision) => {
    if(decision === 'logout'){
      navigate("/logout");
    }else if(decision === 'return'){
      navigate("/supply/requisition-form");
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
    if(decision) {
      clearCart();
      setCart({});
    }
  }

  const checkoutPost = async (deliveryMeth, specialInst) => {
    const customerId = JSON.parse(localStorage.getItem('ess.auth.empId'));
    const deliveryMethod = deliveryMeth;
    const destinationId = JSON.parse(localStorage.getItem('destination')).locId;
    const lineItems = [];
    items.forEach(item => {
      if(cart[item.id]) {
        lineItems.push({
          MAX_QTY: 9999,
          item: item,
          quantity: cart[item.id]
        });
      }
    });
    const specialInstructions = instructions;

    const payload = {
      customerId: customerId,
      deliveryMethod: deliveryMethod,
      destinationId: destinationId,
      lineItems: lineItems,
      specialInstructions: specialInstructions
    }
    console.log(payload);

    try {
      const response = await fetchApiJson('/supply/requisitions', { method: 'POST', payload: payload });
      clearCart();
      setCart({});
      setInstructions('');
    } catch (error) {
      console.error('Fetch error:', error);
    }
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
               <SpecialInstructions
                 openEmptyCartPopup={openEmptyCartPopup}
                 openCheckOutPopup={openCheckOutPopup}
                 instructions={instructions}
                 setInstructions={setInstructions}/>
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
