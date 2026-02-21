function CartSummary({
  cartItems = [],
  onIncrement,
  onDecrement,
  totalAmount = 0,
  onPlaceOrder,
  orderSubmitting = false,
  orderMessage = ""
}) {
  return (
    <aside className="cart-panel">
      <h2>Cart</h2>
      {/* Added ?. safety check here */}
      {!cartItems || cartItems.length === 0 ? (
        <p className="cart-empty">No items added yet.</p>
      ) : (
        <ul className="cart-list">
          {cartItems.map((item) => (
            <li key={item.id}>
              <div>
                <p>{item.name}</p>
                <small>${item.price?.toFixed(2)} each</small>
              </div>
              <div className="qty-controls">
                <button type="button" onClick={() => onDecrement(item.id)}>-</button>
                <span>{item.quantity}</span>
                <button type="button" onClick={() => onIncrement(item.id)}>+</button>
              </div>
            </li>
          ))}
        </ul>
      )}

      <div className="cart-total">
        <span>Total Amount</span>
        <strong>${totalAmount?.toFixed(2) || "0.00"}</strong>
      </div>
      <button
        className="checkout-btn"
        type="button"
        disabled={!cartItems || cartItems.length === 0 || orderSubmitting}
        onClick={onPlaceOrder}
      >
        {orderSubmitting ? "Placing..." : "Place Order"}
      </button>
      {orderMessage && <p className="checkout-message">{orderMessage}</p>}
    </aside>
  );
}

export default CartSummary;
