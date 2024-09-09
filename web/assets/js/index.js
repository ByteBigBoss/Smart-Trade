async function checkSignIn() {

    const response = await fetch(
            "CheckSignIn"
            );


    if (response.ok) {

        const json = await response.json();
        console.log(json);


        const response_dto = json.response_dto;

        if (response_dto.success) {
            //SIGNED IN
            const user = response_dto.content;
            console.log(user);

            let st_quick_link = document.getElementById("st-quick-link");

            let st_quick_link_li_1 = document.getElementById("st-quick-link-li-1");
            st_quick_link_li_1.remove();

            let st_quick_link_li_2 = document.getElementById("st-quick-link-li-2");
            st_quick_link_li_2.remove();

            let new_li_tag1 = document.createElement("li");
            new_li_tag1.innerHTML = user.first_name + " " + user.last_name;
            st_quick_link.appendChild(new_li_tag1);

            let st_btn_1 = document.getElementById("st-btn-1");
            st_btn_1.href = "SignOut";
            st_btn_1.innerHTML = "SignOut";

            let st_div_1 = document.getElementById("st-div-1");
            st_div_1.remove();

        } else {
            //NOT SIGNED IN 
            console.log("Not Signed In");
        }


        //PRODUCTS
        const productList = json.products;


        let i = 1;
        productList.forEach(product => {
            document.getElementById("st-product-title-" + i).innerHTML = product.title;
            document.getElementById("st-product-link-" + i).href = "single-product.html?id=" + product.id;
            document.getElementById("st-product-img-" + i).src = "product-images/" + product.id + "/image1.png";
            document.getElementById("st-product-price-" + i).innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {
                        minimumFractionDigits: 2
                    }
            ).format(product.price);
            i++;
        });


    }

}


async function viewCart(){
    
    const response = await fetch("cart.html");
    
    if(response.ok){
        const cartHtmlText = await response.text();
        
        //PARSE TEXT TO HTML CONTENT
        const parser = new DOMParser();
        const cartHtml = parser.parseFromString(cartHtmlText, "text/html");
        
        const cart_main = cartHtml.querySelector(".main-wrapper");
       
        document.querySelector(".main-wrapper").innerHTML = cart_main.innerHTML;
        loadCartItems();
    }
    
}