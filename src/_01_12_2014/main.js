(function(){
    fs = require('fs');

    fs.readFile("book.txt", {encoding: "utf8"}, function(err, data){
        if(err) throw err;
        var wordArr = data.toLowerCase().replace(/[^a-zA-Z0-9-]+/g, ' ').split(" ");
        var outputArr = {};
        for(var i = 0; i < wordArr.length; i++){
            if (outputArr[wordArr[i]] === undefined){
            outputArr[wordArr[i]] = 1;
            continue;
            }
            else{
            outputArr[wordArr[i]] = outputArr[wordArr[i]] += 1;
            }
        }
        console.log(outputArr);
//        for(var key in outputArr){
//            
//        }
//        fs.writeFile('output.txt', outputArr, function(err){
//            if(err) throw err;
//            console.log('done');
//        });
    });
})();
