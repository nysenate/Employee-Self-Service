import React, { useState } from "react";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import EmptyCart from "app/views/supply/cart/EmptyCart";
import Controls from "app/components/Controls";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { useItems } from "app/views/supply/useItems";
import ItemQuantityControls from "app/views/supply/ItemQuantityControls";
import { Button } from "app/components/Button";
import EmptyCartConfirmation from "app/views/supply/cart/EmptyCartConfirmation";
import { Link } from "react-router-dom";
import DeliveryMethodModal from "app/views/supply/cart/DeliveryMethodModal";
import { useCheckout } from "app/views/supply/cart/useCheckout";
import useAuth from "app/contexts/Auth/useAuth";
import LoadingIndicator from "app/components/LoadingIndicator";
import CheckoutSummaryModal from "app/views/supply/cart/CheckoutSummaryModal";

export default function ShoppingCartIndex() {
  const auth = useAuth();
  const { cart, clearCart, destination } = useSupplyContext();
  const [instructions, setInstructions] = useState("");
  const itemsQuery = useItems();
  const [isEmptyCartConfirmationOpen, setIsEmptyCartConfirmationOpen] =
    useState(false);
  const [isDeliveryMethodModalOpen, setIsDeliveryMethodModalOpen] =
    useState(false);
  const [isCheckoutSummaryOpen, setIsCheckoutSummaryOpen] = useState(false);
  const checkoutApi = useCheckout();
  const [checkoutRes, setCheckoutRes] = useState();

  const onCheckout = (deliveryMethod) => {
    const lineItems = Object.entries(cart.items).map(([itemId, qty]) => ({
      item: itemsQuery.data.get(parseInt(itemId)),
      quantity: parseInt(qty),
    }));
    checkoutApi
      .mutateAsync({
        customerId: auth.empId(),
        deliveryMethod,
        destinationId: destination.locId,
        lineItems,
        specialInstructions: instructions,
      })
      .then((r) => setCheckoutRes(r))
      .then(() => clearCart())
      .then(() => setIsCheckoutSummaryOpen(true));
  };

  if (cart.totalItems === 0) {
    return (
      <>
        <EmptyCart />
        <CheckoutSummaryModal
          isOpen={isCheckoutSummaryOpen}
          setIsOpen={setIsCheckoutSummaryOpen}
          res={checkoutRes}
        />
      </>
    );
  }

  if (itemsQuery.isPending) {
    return <LoadingIndicator />;
  }

  return (
    <div>
      <Hero>Shopping Cart</Hero>
      <Controls className="p-4">
        <span className="text-purple-700 font-semibold">Destination: </span>
        {destination.locId} ({destination.locationDescription})
      </Controls>

      <Card className="my-5">
        <Card.Header>
          <span className="text-lg font-semibold">Cart Items</span>
        </Card.Header>
        {Object.keys(cart.items).map((itemId) => (
          <div key={itemId}>
            <CartItem item={itemsQuery.data.get(parseInt(itemId))} />
            <hr />
          </div>
        ))}
        <div className="bg-gray-50 p-3 flex items-center justify-between gap-2">
          <div>
            <label htmlFor="special-instructions" className="align-middle mr-2">
              Special Instructions:
            </label>
            <textarea
              id="special-instructsion"
              className="align-middle border-1"
              value={instructions}
              onChange={(e) => setInstructions(e.target.value)}
              rows="3"
              cols="50"
            />
          </div>
          <Button
            color="secondary"
            onClick={() => setIsEmptyCartConfirmationOpen(true)}
          >
            Empty Cart
          </Button>
          <Link
            to="/supply/requisition-form"
            style={{ textDecoration: "none" }}
          >
            <Button color="secondary">Continue Browsing</Button>
          </Link>
          <Button onClick={() => setIsDeliveryMethodModalOpen(true)}>
            Checkout
          </Button>
        </div>
      </Card>
      <EmptyCartConfirmation
        isOpen={isEmptyCartConfirmationOpen}
        setIsOpen={setIsEmptyCartConfirmationOpen}
      />
      <DeliveryMethodModal
        isOpen={isDeliveryMethodModalOpen}
        setIsOpen={setIsDeliveryMethodModalOpen}
        onResolve={onCheckout}
      />
    </div>
  );
}

function CartItem({ item }) {
  return (
    <div className="grid grid-cols-12">
      <div className="col-span-3 flex items-center justify-center">
        <img
          className="my-3 h-[120px]"
          alt={item.description}
          src={`/assets/supply_photos/${item.commodityCode}.jpg`}
          onError={({ currentTarget }) => {
            currentTarget.onerror = null; // prevents looping
            currentTarget.src = "/assets/supply_photos/no_photo_available.png";
          }}
        />
      </div>
      <div className="col-span-6 text-xl font-semibold mt-4">
        {item.description}
      </div>
      <div className="col-span-3 flex flex-col items-center justify-center relative">
        <p className="absolute top-9">{item.unit}</p>
        <ItemQuantityControls item={item} />
      </div>
    </div>
  );
}
