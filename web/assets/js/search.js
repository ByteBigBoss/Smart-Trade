async function loadData(){
    
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