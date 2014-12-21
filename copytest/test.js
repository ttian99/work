var fs = require('fs-extra');
var exec = require('child_process').exec;

function copyExcept(src, dst) {
	fs.exists(src, function(exists) {
		if (exists) {
			fs.copySync(src, dst);
		} else {
			console.log(src + 'no exists');
		}
	});
}



function copyExceptCocosLib(src, dst) {
	fs.exists(src, function(exists) {
		if (exists) {
			fs.copySync(src, dst, function(file) {
				if (src.indexOf('libcocos2djs.so') !== -1) {
					return false;
				}
				return true;
			});
		} else {
			console.log(src + '|------------is no exists');
			return;
		}
	});
}

copyExcept('./a/1.txt', './bb/11.txt');
copyExcept('./a/2.txt', './bb/22.txt');
copyExcept('./c', './bb/22.txt');
copyExcept('./a/1', './bb/1');


//打包mingling
// rd /s/q ..\..\proj.android\assets\src
// rd /s/q ..\..\proj.android\assets\res
// mkdir ..\..\proj.android\assets\res
// xcopy /s ..\res ..\..\proj.android\assets\res
// python cocos2d.py jscompile -s ../ -d ..\..\proj.android\assets
// pause



fs.removeSync('./assets/res');
fs.removeSync('./assets/src');
copySync('../star/res', './assets/res');
python = exec('python cocos2d.py jscompile -s');
python.stdout.on('data', function(data) {
	console.log('python进程输出，代码:' + data);
});
python.on('exit', function(code) {
	console.log('python进程已关闭，代码:' + code);
});