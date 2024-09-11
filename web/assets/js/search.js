async function loadData(){
    
    const response = await fetch(
            "LoadData"
            );
    
    if(response.ok){
        
        const json = await response.json();
        
        
        updateProductView(json);
    }else{
        
      
        
    }
}

async function loadData(prefix, dataList, property){
    
    let options = document.getElementById(prefix + "-options");
    let li = document.getElementById(prefix + "-li");
    options.innerHTML = "";
    
    dataList.forEach(data=>{
        let li_clone = li.cloneNode(true);
        
        if(prefix == "color"){
            
            li_clone.style.borderColor = data[property];
            li_clone.querySelector("#"+prefix+"-a").style.backgroundColor = data[property];
            
        }else{
            li_clone.querySelector("#"+prefix+"-a").innerHTML= data[property];
        }
        options.appendChild(li_clone);
    });
    
    const all_li = document.querySelector("#"+prefix+"-options li");
    all_li.forEach(x=>{
        x.addEventListener('click', function(){
            all_li.forEach(y=>y.classList.remove('chosen'));
            this.clssList.add('chosen');
        });
    });
    
}

function searchProducts(){
    
    //INITIALIZE NOTIFICATION
    const popup = Notification();
    
    //GET SEARCH DATA
    let category_name = document.getElementById("category-options")
            .category_ul.querySelector(".chosen")
            ?.querySelector("a").innerHTML; 
    
    
    let condition_name = document.getElementById("condition-options")
            .category_ul.querySelector(".chosen")
            ?.querySelector("a").innerHTML;
    
    
    let color_name = document.getElementById("color-options")
            .category_ul.querySelector(".chosen")
            ?.querySelector("a").style.backgroundColor;
    
    
    let storage_value = document.getElementById("storage-options")
        .category_ul.querySelector(".chosen")
        ?.querySelector("a").innerHTML;

    
    let price_range_start = $('#slider-range').slider('values', 0);
    let price_range_end = $('#slider-range').slider('values', 1);
    
    let sort_text = document.getElementById("st-sort").value;
    
    //END: GET SEARCH DATA
    
    //PREPARE REQUEST DATA
    const data = {
        category_name:category_name,
        condition_name: condition_name,
        color_name: color_name,
        storage_value: storage_value,
        price_range_start:price_range_start,
        price_range_end:price_range_end,
        sort_text:sort_text
    }

    //SEND POST REQUEST
    const response = await fetch(
            "SearchProducts",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );

    //RESPONSE HANDLING
    if (response.ok) {

        //GET RESPONSE JSON
        const json = await response.json();
        console.log(json);
        
        if(json.success){
            
            updateProductView(json);
            
              popup.success({
                message: "Search Completed!"
            });
            
        }else{
              popup.error({
                message: "Try again later!"
            });
        }
        

    } else {
        popup.error({
                message: "Try again later!"
            });
    }
    
}

function updateProductView (json){
    
      //START: PAGINATION
        let st_pagination_container = document.getElementById("st-pagination-container");
        let st_pagination_button = documnet.getElementById("st-pagination-button");
        st_pagination_container.innerHTML  ="";
        
        let product_count = json.allProductCount;
        const product_per_page = 6;
        
        let pages = Math.ceil(product_count / product_per_page);
        
        //ADD PREVIOUS BUTTON
        let st_pagination_button_clone_prev = st_pagination_button.cloneNode();
    
}