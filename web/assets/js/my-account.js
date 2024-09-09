var modelList;

async function loadFeatures() {

    const response = await fetch("LoadFeatures");

    if (response.ok) {

        const json = await response.json();

        const categoryList = json.categoryList;//CATEGORY LIST
        modelList = json.modelList; //MODEL LIST
        const colorList = json.colorList;  //COLOR LIST
        const storageList = json.storageList;   //STORAGE LIST
        const conditionList = json.conditionList;//CONDITION LIST

        loadSelect("categorySelect", categoryList, ["id", "name"]);
//        loadSelect("modelSelect", modelList, ["id", "name"]);
        loadSelect("colorSelect", colorList, ["id", "name"]);
        loadSelect("storageSelect", storageList, ["id", "value"]);
        loadSelect("conditionSelect", conditionList, ["id", "name"]);

    } else {
        document.getElementById("message").innerHTML = "Please try again later";
    }

}

function loadSelect(id, list, props) {
    const selectTag = document.getElementById(id);
    list.forEach(item => {
        let tag = document.createElement("option");
        tag.value = item[props[0]];
        tag.innerHTML = item[props[1]];
        selectTag.appendChild(tag);
    });
}

function updateModels() {

    let modelTag = document.getElementById("modelSelect");
    let selectedCategoryId = document.getElementById("categorySelect").value;
    modelTag.length = 1; //REMOVE ALL VALUES AND KEEP ONLY 1st VALUE

    modelList.forEach(model => {
        if (model.category.id == selectedCategoryId) { //ADD MODELS IN SELECTED CATEGORY
            let tag = document.createElement("option");
            tag.value = model.id;
            tag.innerHTML = model.name;
            modelTag.appendChild(tag);
        }
    });
}

async function productListing() {

    //INPUTS
    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const priceTag = document.getElementById("price");
    const qtyTag = document.getElementById("qty");

    //SELECTS
    const categorySelectTag = document.getElementById("categorySelect");
    const modelSelectTag = document.getElementById("modelSelect");
    const storageSelectTag = document.getElementById("storageSelect");
    const colorSelectTag = document.getElementById("colorSelect");
    const conditionSelectTag = document.getElementById("conditionSelect");

    //PRODUCT IMAGES
    const img1Tag = document.getElementById("img1");
    const img2Tag = document.getElementById("img2");
    const img3Tag = document.getElementById("img3");

    //SHOW MSG
    const msgTag = document.getElementById("message");

    //FROM DATA
    const data = new FormData();
    data.append("title", titleTag.value);
    data.append("description", descriptionTag.value);
    data.append("price", priceTag.value);
    data.append("qty", qtyTag.value);
    data.append("categoryId", categorySelectTag.value);
    data.append("modelId", modelSelectTag.value);
    data.append("storageId", storageSelectTag.value);
    data.append("colorId", colorSelectTag.value);
    data.append("conditionId", conditionSelectTag.value);
    data.append("img1", img1Tag.files[0]);
    data.append("img2", img2Tag.files[0]);
    data.append("img3", img3Tag.files[0]);

    //OPEN REQUEST
    const response = await fetch(
            "ProductListing",
            {
                method: "POST",
                body: data
            }
    );

    //HANDLE RESPONSE
    if (response.ok) {

        const json = await response.json();

        const popup = Notification();

        if (json.success) {

            titleTag.value = "";
            descriptionTag.value = "";
            priceTag.value = "";
            qtyTag.value = 0;

            categorySelectTag.value = 0;
            modelSelectTag.length = 1;
            storageSelectTag.value = 0;
            colorSelectTag.value = 0;
            conditionSelectTag.value = 0;

            img1Tag.value = null;
            img2Tag.value = null;
            img3Tag.value = null;

            popup.success({
                message: json.content
            });
        } else {
            popup.error({
                message: json.content
            });
        }

    } else {
        //BAD RESPONSE
        msgTag.innerHTML = "Please try again later!";
    }

}