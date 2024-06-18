// cartUtils.js

// Function to increment the item quantity in the cart
export const incrementItem = (itemId) => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  cart[itemId] = (cart[itemId] || 0) + 1;
  localStorage.setItem('cart', JSON.stringify(cart));
};

export const updateItemQuantity = (itemId, quantity) => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  cart[itemId] = quantity;
  if(quantity===0) delete cart[itemId];
  localStorage.setItem('cart', JSON.stringify(cart));
};

// Function to decrement the item quantity in the cart
export const decrementItem = (itemId) => {
  const cart = JSON.parse(localStorage.getItem('cart')) || {};
  if (cart[itemId] > 1) {
    cart[itemId] -= 1;
  } else {
    delete cart[itemId];
  }
  localStorage.setItem('cart', JSON.stringify(cart));
};

// Function to clear the cart
export const clearCart = () => {
  localStorage.removeItem('cart');
  console.log('Cart has been cleared.');
};



//Example use in pages::
// import { incrementItem, decrementItem, clearCart } from '../cartUtils'; // Adjust the path as necessary
//
// const CartComponent = () => {
//   const [cart, setCart] = useState(() => JSON.parse(localStorage.getItem('cart')) || {});
//
//   useEffect(() => {
//     localStorage.setItem('cart', JSON.stringify(cart));
//   }, [cart]);
//
//   const handleIncrement = (itemId) => {
//     incrementItem(itemId);
//     setCart(JSON.parse(localStorage.getItem('cart')));
//   };
//
//   const handleDecrement = (itemId) => {
//     decrementItem(itemId);
//     setCart(JSON.parse(localStorage.getItem('cart')));
//   };
//
//   const handleClearCart = () => {
//     clearCart();
//     setCart({});
//   };