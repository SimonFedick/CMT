import CommunicationManager from "../../communication/CommunicationManager.js";
import GetPreviousVotingsRequestPacket from "../../communication/packets/GetPreviousVotingsRequestPacket.js";


export function getPreviousVote(){

	function success(packet){

        if(packet.result === "Valid"){
			
			displayPreviousVotes(packet);


		}

    }

    function fail() {
        console.log("Something went wrong during, get active vote question & options.");
    }

    const previousVote = new GetPreviousVotingsRequestPacket();

    CommunicationManager.send(previousVote, success, fail); 
}

$(document).ready(getPreviousVote);
    
function displayPreviousVotes(packet){
    console.log(packet);

    //Display message if there haven't been any votings yet
    if(packet.votings.length === 0){
        //Maybe fix styling?
        $('#accordion').append("<h2 class='contact-title text-container' style='margin-left: 25px;' data-text-section ='voting' data-text-elem='noPrevVoting'>"+window.languageData["voting"]["noPrevVoting"]+"</h2>");
    }

    for(var i in packet.votings){

    var voteQuestion = '<div class="card" style="margin-bottom:0px;">'+
                    '<div class="card-header" id="headingTwo" style="padding-top: 25px;">'+
                    '<h5 class="mb-0" style="margin-left: 30px;">'+
                        packet.votings[i].question +
                    '<button class="btn btn-link collapsed" data-toggle="collapse"'+
                    'data-target="#collapse'+packet.votings[i].ID+'" aria-expanded="false" aria-controls="collapse'+packet.votings[i].ID+'" style="word-break: break-word;">'+
                    '<i style="font-size: medium" class="text-container" data-text-section ="voting" data-text-elem="seeFullDetails">'+window.languageData["voting"]["seeFullDetails"]+'</i>'+
                    '</button>'+
                    '</h5>'+
                    '</div>'+
                    '<div id="collapse'+packet.votings[i].ID+'" class="collapse" aria-labelledby="headingTwo" data-parent="#accordion">' +
                    '<ul>' +
                    '<div class="card-body" id="'+packet.votings[i].ID+'prev">'+

                    '</div>'+
                    '</ul>' +
                    '</div>'+
                    '</div>';

        $('#accordion').append(voteQuestion);

        var totalAttendees = 0;
        var percentageValue = 0;
        var voterName = null;


        for( var k in packet.votings[i].options){
            totalAttendees =  totalAttendees + packet.votings[i].options[k].publicVotes;
            // console.log(totalAttendees);
            // console.log("testing");
        }

        var voteOptions = '<span style="font-weight:bold;" class="text-container" data-text-section ="voting" data-text-elem="totalNumberOfVoters">'+window.languageData["voting"]["totalNumberOfVoters"]+'</span> ' + totalAttendees + '<br><br>';
        $('#'+packet.votings[i].ID+'prev').append(voteOptions);
        
        //+ 'Vote Option ' + (packet.votings[i].options[j].optionID + 1) + ': '
        for(var j in packet.votings[i].options){
            percentageValue = Math.round((packet.votings[i].options[j].publicVotes/totalAttendees)*100);
            voterName = packet.votings[i].options[j].votersname; 

            voteOptions =  "<span class='text-container' data-text-section ='voting' data-text-elem='voteOption'>"+window.languageData["voting"]["voteOption"]+ " </span>" + (packet.votings[i].options[j].optionID + 1) + ': '
                                + packet.votings[i].options[j].name + '<br>' + '<span class="text-container" data-text-section ="voting" data-text-elem="numberOfVotrers">'+window.languageData["voting"]["numberOfVotrers"]+'</span>'
                                + packet.votings[i].options[j].publicVotes + '<br>'
                                + '<span style="font-weight:bold;">' + (isNaN(percentageValue)? 0 : percentageValue) + '% </span>' + '<span class="text-container" data-text-section ="voting" data-text-elem="ofTotalNumberOfVoters">'+window.languageData["voting"]["ofTotalNumberOfVoters"]+'</span>';

            //Name of Voters Section only should be displayed in case the voting is a named one
            if(packet.votings[i].namedVote){
                voteOptions += '<br><span class="text-container" data-text-section ="voting" data-text-elem="nameOfVoters">'+window.languageData["voting"]["nameOfVoters"]+'</span>: ' + (voterName == undefined || voterName.length == 0? '<span class="text-container" data-text-section ="voting" data-text-elem="notAvailable">'+window.languageData["voting"]["notAvailable"]+'</span>': voterName.join(', '))+ '<br><br>';
            } else{
                voteOptions += '<br><br>';
            }
                                // + '<br>'
                                // + '</pre>';
            // if(packet.votings[i].namedVote){
            //     for(var name in packet.votings[i].options[k].voters) {
            //         var namesOfAttendee = namesofAttendee + packet.votings[i].options[k].voters[name]  
            //     }
            // }
            
           
            $('#'+packet.votings[i].ID+'prev').append(voteOptions);
        }

    }

}