var fs = require('fs');

function copyExcept(src,dst){
	fs.exists(src,function(exists){
		if (exists){
			fs.writeFileSync(dst,fs.readFileSync(src));
		} else {
			console.log('no exists');
		}
	});
}

copyExcept('./a/1.txt','./bb/11.txt');
copyExcept('./a/2.txt','./bb/22.txt');


