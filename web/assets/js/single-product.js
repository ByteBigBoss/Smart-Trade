async function loadProduct() {

    const parameters = new URLSearchParams(window.location.search);

    if (parameters.has("id")) {
        const productId = parameters.get("id");

        const response = await fetch("LoadSingleProduct?id=" + productId);

        if (response.ok) {

            const json = await response.json();
//            console.log(json.product);            
            console.log(json.product);

            const id = json.product.id;
            document.getElementById("img1").src = "product-images/" + id + "/image1.png";
            document.getElementById("img2").src = "product-images/" + id + "/image2.png";
            document.getElementById("img3").src = "product-images/" + id + "/image3.png";

            document.getElementById("img1-thumb").src = "product-images/" + id + "/image1.png";
            document.getElementById("img2-thumb").src = "product-images/" + id + "/image2.png";
            document.getElementById("img3-thumb").src = "product-images/" + id + "/image3.png";

            document.getElementById("product-title").innerHTML = json.product.title;
            document.getElementById("product-published-on").innerHTML = json.product.date_time;
            document.getElementById("product-price").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(json.product.price);

            document.getElementById("product-category").innerHTML = json.product.model.category.name;
            document.getElementById("product-model").innerHTML = json.product.model.name;
            document.getElementById("product-condition").innerHTML = json.product.product_condition.name;
            document.getElementById("product-qty").innerHTML = json.product.qty;

            document.getElementById("color-border").style.borderColor = json.product.color.name;
            document.getElementById("color-background").style.backgroundColor = json.product.color.name;

            document.getElementById("product-storage").innerHTML = json.product.storage.value;
            document.getElementById("product-description").innerHTML = json.product.description;

            //ADD EVENT LISTENER 
            document.getElementById("add-to-cart-main")
                    .addEventListener(
                            "click",
                            (e) => {

                        addToCart(id, document.getElementById("add-to-cart-qty").value);
                        e.preventDefault();
                    });

            //GET SIMILER PRODUCT
            let ProductHtml = document.getElementById("similer-product");

            //CLAER CONTAINER
            document.getElementById("similer-product-main").innerHTML = "";

            json.productList.forEach(item => {
                let productCloneHtml = ProductHtml.cloneNode(true);


                //CLONE
                productCloneHtml.querySelector("#similer-product-img").src = "product-images/" + item.id + "/image1.png";


                productCloneHtml.querySelector("#similer-product-a1").href = "single-product.html?id=" + item.id;
                productCloneHtml.querySelector("#similer-product-a2").href = "single-product.html?id=" + item.id;
                productCloneHtml.querySelector("#similer-product-title").innerHTML = item.title;
                productCloneHtml.querySelector("#similer-product-storage").innerHTML = item.storage.value;
                productCloneHtml.querySelector("#similer-product-price").innerHTML = "Rs. " + new Intl.NumberFormat(
                        "en-US",
                        {
                            minimumFractionDigits: 2
                        }
                ).format(item.price);
                productCloneHtml.querySelector("#similer-product-color").style.backgroundColor = item.color.name;
                productCloneHtml.querySelector("#similer-product-color-border").style.borderColor = item.color.name;

                productCloneHtml.querySelector("#similer-product-add-to-cart")
                        .addEventListener(
                                "click",
                                (e) => {
                            addToCart(item.id, 1);
                            e.preventDefault();
                        });



                document.getElementById("similer-product-main").appendChild(productCloneHtml);


            });

            $('.recent-product-activation').slick({
                infinite: true,
                slidesToShow: 4,
                slidesToScroll: 4,
                arrows: true,
                dots: false,
                prevArrow: '<button class="slide-arrow prev-arrow"><i class="fal fa-long-arrow-left"></i></button>',
                nextArrow: '<button class="slide-arrow next-arrow"><i class="fal fa-long-arrow-right"></i></button>',
                responsive: [{
                        breakpoint: 1199,
                        settings: {
                            slidesToShow: 3,
                            slidesToScroll: 3
                        }
                    },
                    {
                        breakpoint: 991,
                        settings: {
                            slidesToShow: 2,
                            slidesToScroll: 2
                        }
                    },
                    {
                        breakpoint: 479,
                        settings: {
                            slidesToShow: 1,
                            slidesToScroll: 1
                        }
                    }
                ]
            });

        } else {
            window.location = "index.html";
        }

    } else {
        window.location = "index.html";
    }

}

async function addToCart(id, qty) {

    const response = await fetch(
            "AddToCart?id=" + id + "&qty=" + qty
            );
    
    const popup = Notification();

    if (response.ok) {

        const json = await response.json();
        if(json.success){
            
            popup.success({
                message: json.content
            });
        }else{
            popup.error({
                message: json.content
            });
        }
        

    } else {
        popup.error({
                message: "Unable to process your request"
            });
    }

}