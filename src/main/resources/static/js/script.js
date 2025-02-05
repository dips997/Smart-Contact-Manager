console.log("this is script File");

const toggleSidebar = () => {
  if ($(".sidebar").is(":visible")) {
    //true
    //band karna he
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");
  } else {
    //false
    //show karna he
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }
};

const search = () => {
  let query = $("#search-input").val();

  if (query == "") {
    $(".search-result").hide();
  } else {
    //sending request to server
    let url = `http://localhost:8080/search/${query}`;

    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        //console.log(data);
        let text = `<div class='list-group'>`;

        data.forEach((contact) => {
          text += `<a href="/user/${contact.cId}/contact"  class="list-group-item list-group-item-action"> 
						<img class="my_profile_image" src="/img/${contact.image}" alt="my_pic" >&nbsp; ${contact.name} (${contact.secondName})</a>`;
        });

        text += `</div>`;

        $(".search-result").html(text);
        $(".search-result").show();
      });

    $(".search-result").show();
  }
};

//first request to server to create order

const paymentStart = () => {
  console.log("payment started....");

  var amount = $("#payment_field").val();

  console.log(amount);

  if ((amount == "") | (amount == null)) {
    //   alert("amount is required..");
    swal("Failed!", "Amount is Required !!", "error");
    return;
  }

  //code....
  //we will use ajax to send  request to server to create order- jquery
  $.ajax({
    url: "/user/create_order",
    data: JSON.stringify({ amount: amount, info: "order_request" }),
    contentType: "application/json",
    type: "POST",
    dataType: "json",
    success: function (response) {
      //invoked when success
      console.log(response);
      if (response.status == "created") {
        //open payment form
        let options = {
          key: "",
          amount: response.amount,
          currency: "INR",
          name: "Smart Contact Manager",
          description: "Donation",
          image:
            "https://images.unsplash.com/photo-1620632523414-054c7bea12ac?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          order_id: response.id,
          handler: function (response) {
            console.log(response.razorpay_payment_id);
            console.log(response.razorpay_order_id);
            console.log(response.razorpay_signature);
            console.log("payment successful !!");
            //alert("congrates !! Payment successful !!");
            updatePaymentOnServer(
              response.razorpay_payment_id,
              response.razorpay_order_id,
              "paid"
            );
          },
          prefill: {
            //We recommend using the prefill parameter to auto-fill customer's contact information especially their phone number
            name: "", //your customer's name
            email: "",
            contact: "", //Provide the customer's phone number for better conversion rates
          },
          notes: {
            address: "Smart Contact Manager Dipak",
          },
          theme: {
            color: "#3399cc",
          },
        };

        let rzp = new Razorpay(options);
        rzp.on("payment.failed", function (response) {
          console.log(response.error.code);
          console.log(response.error.description);
          console.log(response.error.source);
          console.log(response.error.step);
          console.log(response.error.reason);
          console.log(response.error.metadata.order_id);
          console.log(response.error.metadata.payment_id);
          alert("Oops Payment Failed !!");
        });
        rzp.open();
      }
    },
    error: function (error) {
      //invoked when error
      console.log(error);
      alert("something went wrong !!");
    },
  });
};

function updatePaymentOnServer(payement_id, order_id, status) {
  $.ajax({
    url: "/user/update_order",
    data: JSON.stringify({
      payment_id: payement_id,
      order_id: order_id,
      status: status,
    }),
    contentType: "application/json",
    type: "POST",
    dataType: "json",
    success: function (response) {
      swal("Good job!", "congrates !! Payment successful !!", "success");
    },
    error: function (error) {
      swal(
        "Failed!",
        "Your payment is successful, but we did not get on server, we will contact you as soon as possible",
        "error"
      );
    },
  });
}
