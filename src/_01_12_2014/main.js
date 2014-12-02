(function(){
    var fs = require('fs');

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
        var outputStrBuilder = "";
        for(var key in outputArr){
            outputStrBuilder += key + ":" + outputArr[key] + "\n";
        }
        fs.writeFile('output.txt', outputStrBuilder, function(err){
            if(err) throw err;
            console.log('done');
        });
    });
})();
