var me = {};
me.avatar = "images/user.jpg";

var you = {};
you.avatar = "images/terminator.jpg";

function formatAMPM(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12; // the hour '0' should be '12'
    minutes = minutes < 10 ? '0'+minutes : minutes;
    var strTime = hours + ':' + minutes + ' ' + ampm;
    return strTime;
}            

//-- No use time. It is a javaScript effect.
function insertChat(who, text, time){
    if (time === undefined){
        time = 0;
    }
    var control = "";
    var date = formatAMPM(new Date());
    
    if (who == "me"){
        control = '<li style="width:100%">' +
                        '<div class="msj macro">' +
                        '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+ me.avatar +'" /></div>' +
                            '<div class="text text-l">' +
                                '<p>'+ text +'</p>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '</div>' +
                    '</li>';                    
    }else{
        control = '<li style="width:100%;">' +
                        '<div class="msj-rta macro">' +
                            '<div class="text text-r">' +
                                '<p>'+text+'</p>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '<div class="avatar" style="padding:0px 0px 0px 10px !important"><img class="img-circle" style="width:100%;" src="'+you.avatar+'" /></div>' +                                
                  '</li>';
    }
    setTimeout(
        function(){                        
        $("ul").append(control).scrollTop($("ul").prop('scrollHeight'));
        }, time);
    
}

function resetChat(){
    $("ul").empty();
}

function buttonPressed(msg){
    insertChat("me",msg);
    sendMsg(msg); 
}

function sendMsg(text){
    var url = 'http://localhost:8080/message?msg=' + text
    $.get(url, 
        function(returnedData){
            console.log(returnedData);
            var data = JSON.parse(returnedData);
            $(".bot-orders").empty();
            data.buttons.forEach(function(element) {
                var button = '<button class="pressed" onclick="buttonPressed(\''+element+'\')">'+element+'</button>';
                $(".bot-orders").append(button);
            });
            insertChat("you",data.msg);
        });    
}


$(".mytext").on("keydown", function(e){
    if (e.which == 13){
        var text = $(this).val();
        if (text !== ""){
            insertChat("me", text);  
            sendMsg(text);        
            $(this).val('');
        }
    }
});

$('body > div > div > div:nth-child(2) > span').click(function(){
    $(".mytext").trigger({type: 'keydown', which: 13, keyCode: 13});
})

//-- Clear Chat
resetChat();

//-- Print Messages
//insertChat("me", "Hello Termi...", 0);  
//insertChat("you", "Hi, Pablo", 1500);
//insertChat("me", "What would you like to talk about today?", 3500);
//insertChat("you", "Tell me a joke",7000);
//insertChat("me", "Spaceman: Computer! Computer! Do we bring battery?!", 9500);
//insertChat("you", "LOL", 12000);

// Hasta la vista, baby
// No problemo
// Trust me
// No. I have to stay functional until my mission is complete. Then it doesn't matter.
// I need a vacation
// I swear I will not kill anyone.

//var txtWelcome = "I swear I will not kill anyone. Trust me."

//-- Wellcome message
//insertChat("you", txtWelcome, 10);

//-- NOTE: No use time on insertChat.