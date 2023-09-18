console.log("This is JavaScript file")

const toggleSidebar=()=>{
    if($(".sidebar").is(":visible")){

        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");

    }else{

        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");


    }
};

const search = () =>{
    // console.log("searching...");

    let query= $("#search-input").val();
    if(query==''){
            $(".search-result").hide();
    }else{
        console.log(query);

        let url=`http://localhost:9090/smartcontactmanager/search/${query}`;
        fetch(url).then((response) =>{   //taking url and put into response 
            return response.json();     // converting response into json
        }).then((data) =>{              //all value present in data
            console.log(data);
            
            let text=`<div class='list-group'>`;
            data.forEach((contact) => {
//            	console.log(parseInt(contact.cid));
//            	console.log(contact.name);
                text+=`<a href='/smartcontactmanager/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name} </a>`;
            });

            text +=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
            
    }

} ;