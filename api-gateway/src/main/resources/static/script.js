const API_BASE = "";
const CUSTOMER_EMAIL = "test@example.com";


// ==============================
// Navigation
// ==============================

function showSection(sectionName) {

    document.querySelectorAll(".section").forEach(section => {
        section.classList.add("hidden");
    });

    document.getElementById(sectionName).classList.remove("hidden");

    if (sectionName === "cakes") {
        loadCakes();
    }

    if (sectionName === "basket") {
        loadBasket();
    }

    if (sectionName === "orders") {
        loadOrders();
    }
}


// ==============================
// Cakes
// ==============================

async function loadCakes() {

    const cakeList = document.getElementById("cake-list");

    cakeList.innerHTML = "<p>Loading cakes...</p>";

    try {

        const response = await fetch(`${API_BASE}/cakes`);

        if (!response.ok) {
            throw new Error("Could not load cakes");
        }

        const cakes = await response.json();

        displayCakes(cakes);

    } catch (error) {

        console.error(error);

        cakeList.innerHTML =
            "<p>Could not connect to the CakeDelight server.</p>";
    }
}


function displayCakes(cakes) {

    const cakeList = document.getElementById("cake-list");

    cakeList.innerHTML = "";

    if (cakes.length === 0) {
        cakeList.innerHTML = "<p>No cakes found.</p>";
        return;
    }

    cakes.forEach(cake => {

        const card = document.createElement("div");
        card.className = "cake-card";

        card.innerHTML = `
            ${cake.imageUrl
            ? `<img src="images/${cake.imageUrl}" alt="${cake.name}">`
            : `<div style="height:180px; display:flex; align-items:center; justify-content:center; font-size:60px;">🍰</div>`
        }

            <div class="cake-card-content">

                <h3>${cake.name}</h3>

                <p>${cake.description || ""}</p>

                <p>Category: ${cake.category || "Cake"}</p>

                <p class="price">
                    ₹${Number(cake.price).toFixed(2)}
                </p>

                <p>
                    ${cake.available ? "Available" : "Unavailable"}
                </p>

                <button
                    class="add-button"
                    onclick="addToBasket(${cake.id})"
                    ${!cake.available ? "disabled" : ""}
                >
                    Add to Basket
                </button>

                <button
                    class="add-button"
                    onclick="viewRatings(${cake.id})"
                >
                    ⭐ View Ratings
                </button>

            </div>
        `;

        cakeList.appendChild(card);
    });
}

async function filterCakes() {

    const name = document.getElementById("filter-name").value.trim();
    const category = document.getElementById("filter-category").value;
    const minPrice = document.getElementById("filter-min-price").value;
    const maxPrice = document.getElementById("filter-max-price").value;

    const params = new URLSearchParams();

    if (name) {
        params.append("name", name);
    }

    if (category) {
        params.append("category", category);
    }

    if (minPrice) {
        params.append("minPrice", minPrice);
    }

    if (maxPrice) {
        params.append("maxPrice", maxPrice);
    }

    try {

        const response = await fetch(
            `${API_BASE}/cakes/filter?${params.toString()}`
        );

        if (!response.ok) {
            throw new Error("Could not filter cakes");
        }

        const cakes = await response.json();

        displayCakes(cakes);

    } catch (error) {

        console.error(error);

        document.getElementById("cake-list").innerHTML =
            "<p>Could not filter cakes.</p>";
    }
}

function clearFilters() {

    document.getElementById("filter-name").value = "";
    document.getElementById("filter-category").value = "";
    document.getElementById("filter-min-price").value = "";
    document.getElementById("filter-max-price").value = "";

    loadCakes();
}

// ==============================
// Basket
// ==============================

async function addToBasket(cakeId) {

    try {

        const response = await fetch(
            `${API_BASE}/orders/basket/${CUSTOMER_EMAIL}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    cakeId: cakeId,
                    quantity: 1
                })
            }
        );

        if (!response.ok) {
            throw new Error("Could not add item");
        }

        alert("Cake added to basket! 🍰");

    } catch (error) {

        console.error(error);

        alert("Could not add the cake to the basket.");
    }
}


async function loadBasket() {

    const basketList = document.getElementById("basket-list");
    const totalElement = document.getElementById("basket-total");

    basketList.innerHTML = "<p>Loading basket...</p>";

    try {

        const response = await fetch(
            `${API_BASE}/orders/basket/${CUSTOMER_EMAIL}`
        );

        if (!response.ok) {
            throw new Error("Could not load basket");
        }

        const basket = await response.json();

        basketList.innerHTML = "";

        if (basket.length === 0) {

            basketList.innerHTML =
                "<p>Your basket is empty.</p>";

            totalElement.textContent = "Total: ₹0.00";

            return;
        }

        basket.forEach(item => {

            const div = document.createElement("div");

            div.className = "basket-item";

            div.innerHTML = `
                <strong>Cake ID:</strong> ${item.cakeId}
                <br>
                <strong>Quantity:</strong> ${item.quantity}

                <br>

                <button onclick="updateBasketItem(${item.id}, ${item.quantity - 1})">
                    −
                </button>

                <button onclick="updateBasketItem(${item.id}, ${item.quantity + 1})">
                    +
                </button>

                <button onclick="removeBasketItem(${item.id})">
                    Remove
                </button>
            `;

            basketList.appendChild(div);
        });

        await loadBasketTotal();

    } catch (error) {

        console.error(error);

        basketList.innerHTML =
            "<p>Could not load basket.</p>";
    }
}


async function updateBasketItem(itemId, quantity) {

    if (quantity <= 0) {
        await removeBasketItem(itemId);
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE}/orders/basket/${CUSTOMER_EMAIL}/${itemId}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    quantity: quantity
                })
            }
        );

        if (!response.ok) {
            throw new Error("Could not update basket");
        }

        await loadBasket();

    } catch (error) {

        console.error(error);

        alert("Could not update basket.");
    }
}


async function removeBasketItem(itemId) {

    try {

        const response = await fetch(
            `${API_BASE}/orders/basket/${CUSTOMER_EMAIL}/${itemId}`,
            {
                method: "DELETE"
            }
        );

        if (!response.ok) {
            throw new Error("Could not remove item");
        }

        await loadBasket();

    } catch (error) {

        console.error(error);

        alert("Could not remove item.");
    }
}


async function loadBasketTotal() {

    const totalElement = document.getElementById("basket-total");

    try {

        const response = await fetch(
            `${API_BASE}/orders/basket/${CUSTOMER_EMAIL}/total`
        );

        if (!response.ok) {
            throw new Error("Could not calculate total");
        }

        const total = await response.json();

        totalElement.textContent =
            `Total: ₹${Number(total).toFixed(2)}`;

    } catch (error) {

        console.error(error);

        totalElement.textContent =
            "Total: Unable to calculate";
    }
}


// ==============================
// Checkout
// ==============================

async function checkout() {

    try {

        const response = await fetch(
            `${API_BASE}/orders/checkout/${CUSTOMER_EMAIL}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {

            const errorText = await response.text();

            throw new Error(errorText);
        }

        const order = await response.json();

        alert(
            `Order placed successfully! 🎉\nOrder ID: ${order.id}`
        );

        showSection("basket");

    } catch (error) {

        console.error(error);

        alert(
            "Checkout failed. Please make sure your basket is not empty."
        );
    }
}


// ==============================
// Orders
// ==============================

async function loadOrders() {

    const orderList = document.getElementById("order-list");

    orderList.innerHTML = "<p>Loading orders...</p>";

    try {

        const response = await fetch(
            `${API_BASE}/orders`
        );

        if (!response.ok) {
            throw new Error("Could not load orders");
        }

        const orders = await response.json();

        orderList.innerHTML = "";

        if (orders.length === 0) {

            orderList.innerHTML =
                "<p>No orders found.</p>";

            return;
        }

        orders.forEach(order => {

            const div = document.createElement("div");

            div.className = "order-card";

            div.innerHTML = `
                <strong>Order ID:</strong> ${order.id}
                <br>
                <strong>Cake ID:</strong> ${order.cakeId}
                <br>
                <strong>Quantity:</strong> ${order.quantity}
                <br>
                <strong>Total:</strong>
                ₹${Number(order.totalAmount || 0).toFixed(2)}
                <br>
                <strong>Status:</strong> ${order.status}
            `;

            orderList.appendChild(div);
        });

    } catch (error) {

        console.error(error);

        orderList.innerHTML =
            "<p>Could not load orders.</p>";
    }
}


// ==============================
// Ratings
// ==============================

let currentRatingCakeId = null;

async function viewRatings(cakeId) {

    currentRatingCakeId = cakeId;

    const modal = document.getElementById("rating-modal");
    const ratingList = document.getElementById("rating-list");
    const ratingMessage = document.getElementById("rating-message");

    ratingMessage.textContent = "";

    modal.classList.remove("hidden");

    ratingList.innerHTML = "<p>Loading ratings...</p>";

    try {

        const response = await fetch(
            `${API_BASE}/ratings`
        );

        if (!response.ok) {
            throw new Error("Could not load ratings");
        }

        const ratings = await response.json();

        const cakeRatings =
            ratings.filter(rating => rating.cakeId === cakeId);

        if (cakeRatings.length === 0) {

            ratingList.innerHTML =
                "<p>No ratings yet for this cake. Be the first to review it!</p>";

            return;
        }

        const average =
            cakeRatings.reduce(
                (sum, rating) => sum + rating.rating,
                0
            ) / cakeRatings.length;

        let html = `
            <p>
                <strong>Average Rating:</strong>
                ${average.toFixed(1)} ⭐
            </p>
        `;

        cakeRatings.forEach(rating => {

            html += `
                <div class="rating-item">

                    <strong>
                        ${rating.customerName}
                    </strong>

                    <p>
                        ${"⭐".repeat(rating.rating)}
                    </p>

                    <p>
                        ${rating.review || ""}
                    </p>

                </div>
            `;
        });

        ratingList.innerHTML = html;

    } catch (error) {

        console.error(error);

        ratingList.innerHTML =
            "<p>Could not load ratings.</p>";
    }
}

function closeRatingModal() {

    document
        .getElementById("rating-modal")
        .classList.add("hidden");

    currentRatingCakeId = null;
}

async function submitRating() {

    const customerName =
        document.getElementById("rating-customer-name").value.trim();

    const rating =
        Number(document.getElementById("rating-value").value);

    const review =
        document.getElementById("rating-review").value.trim();

    const message =
        document.getElementById("rating-message");

    if (!customerName) {
        message.textContent = "Please enter your name.";
        return;
    }

    if (!review) {
        message.textContent = "Please write a review.";
        return;
    }

    try {

        const response = await fetch(
            `${API_BASE}/ratings`,
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    cakeId: currentRatingCakeId,
                    customerName: customerName,
                    rating: rating,
                    review: review
                })
            }
        );

        if (!response.ok) {
            throw new Error("Could not submit rating");
        }

        message.textContent =
            "Rating submitted successfully! ⭐";

        document.getElementById(
            "rating-customer-name"
        ).value = "";

        document.getElementById(
            "rating-review"
        ).value = "";

        // Reload the ratings
        await viewRatings(currentRatingCakeId);

    } catch (error) {

        console.error(error);

        message.textContent =
            "Could not submit rating. Please try again.";
    }
}


// ==============================
// Initial load
// ==============================

loadCakes();