var fs = require('fs');
// var EventEmitter = require('events').EventEmitter;
var pro = require('child_process').execFile;
var exec = require('child_process').exec;
var readline = require('readline');
var version, lang, part, tag;
var count = 0;
// 调用第三方包xml-digester来解析xml
var xml_digester = require("xml-digester");
var digester = xml_digester.XmlDigester({});
var _logger = xml_digester._logger;
_logger.level(_logger.TRACE_LEVEL);
// 调用prompt包来进行输入
var prompt = require('prompt');

var arr = ['cmcc', 'leren', 'tencent', 'cucc', 'zhangle', 'zhangle_yidongMM_sdk', 'yingyongbao', '360sdk', 'All'];
var arr_lang = ['chinese', 'english'];

// 创建一个prompt用的对象
var schema = {
    properties: {
        lang: {
            description: '请选语言版本:[1]中文(chinese) [2]英文(english)',
            //default: '1',
            pattern: /^[1-2]$/,
            message: ' 输入参数不正确',
            required: true
        },
        part: {
            description: '请选择渠道: [1]移动MM  [2]乐人  [3]腾讯  [4]联通  [5]掌乐  [6]掌乐(移动妹妹)  [7]应用宝  [8]360sdk  [9]以上所有渠道.',
            pattern: /^[0-9]$/,
            message: '请输入0-9之间的整数'
        }
    }
};

// 运行语言选择输入和渠道选择输入函数
function userInput() {

        prompt.start();

        prompt.get(schema, function(err, result) {
            var _part = result.part;
            var _lang = result.lang;
            lang = arr_lang[_lang - 1];
            copy_res(lang);
            if (_part == 9) {
                console.log('打包全部渠道');
                part = arr[count];
                console.log(part);

            } else {
                part = arr[_part - 1];
                count = _part + 100;
            }
            console.log('---------输入信息----------------------------');
            console.log('  你选择的语言为 ' + _lang + ' ' + lang);
            console.log('  您选择的渠道为 ' + _part + ' ' + part);
            console.log('  count数值为' + count);
            // 开始版本号读取函数
            apkVersion(part);
        });

    }
    // 拷贝文件操作
var copy = function(src, dst) {
    // 读取目录中的所有目录和文件
    var files = [];
    files = fs.readdirSync(src);
    files.forEach(function(path) {
        // 屏蔽复制libcocos2djs.so文件
        if (path != "libcocos2djs.so") {
            var _src = src + '/' + path,
                _dst = dst + '/' + path,
                readable, writable;
            //// 判断是否为文件
            if (fs.statSync(_src).isFile()) {
                // 创建读取流
                readable = fs.createReadStream(_src);
                // 创建写入流
                writable = fs.createWriteStream(_dst);
                // 通过管道来传输流
                readable.pipe(writable);

                // 拷贝文件名输出
                console.log("copy||" + _dst + "...");
            }
            // 如果是目录则递归调用自身
            else if (fs.statSync(_src).isDirectory()) {
                exists(_src, _dst, copy);
            }
        }
    });
};

// 在复制目录前需要判断该目录是否存在，不存在需要先创建目录
var exists = function(src, dst, callback) {
    if (fs.existsSync(dst)) {
        callback(src, dst);
    } else {
        fs.mkdirSync(dst);
        callback(src, dst);
    }
};

// 删除文件操作
var deleteDir = function(path) {
    var files = [];
    if (fs.existsSync(path)) {
        if (fs.statSync(path).isFile()) {
            fs.unlinkSync(path);
            return;
        }

        files = fs.readdirSync(path);

        files.forEach(function(file, index) {

            var curPath = path + "/" + file;

            if (fs.statSync(curPath).isDirectory()) { // 迭代

                deleteDir(curPath);

            } else { // 删除文件
                if (file != "libcocos2djs.so") {
                    fs.unlinkSync(curPath);
                }
            }

        });

        if (path != "./libs" && path != "./libs/armeabi") {
            fs.rmdirSync(path);
        }
    }
};

// 替换字符串操作
function replaceStringOfJava(fromId, toId) {
    var path = './src/com/methodsRun/methodsRun.java';
    var str = fs.readFileSync(path).toString();
    str = str.replace(fromId, toId);
    fs.writeFileSync(path, str, 'utf-8');
}

// 清除文件操作
function clearFiles() {
    deleteDir("./src");
    deleteDir("./sdk");
    deleteDir("./res");
    deleteDir("./libs");
    deleteDir("./alipay_lib");
    deleteDir("./runtime");
    deleteDir("./AndroidManifest.xml");
    deleteDir("./.classpath");
    deleteDir("./project.properties");
    deleteDir("./assets/res/loading/game_logo2.png");
    deleteDir("./assets/res/loading/game_logo.png");
}

// 拷贝文件操作
function loadFiles(part) {
    if (part == "cmcc") {
        //拷贝files(来消星星的你)
        exists('../thirPart/cmcc/src', './src', copy);
        //exists( '../thirPart/cmcc/sdk', './sdk', copy);
        exists('../thirPart/cmcc/res', './res', copy);
        exists('../thirPart/cmcc/xml', './', copy);
        exists('../thirPart/cmcc/libs', './libs', copy);
        exists('../thirPart/cmcc/alipay_lib', './alipay_lib', copy);
        //拷贝icon
        exists('../star/resources/popostar', './assets/res/loading', copy);
        //拷贝superegg资源
        exists('../star/resources/cmcc/superegg', './assets/res/superegg', copy);
    } else if (part == "leren") {
        //拷贝files(星星去哪儿)
        exists('../thirPart/leren/src', './src', copy);
        exists('../thirPart/leren/sdk', './sdk', copy);
        exists('../thirPart/leren/res', './res', copy);
        exists('../thirPart/leren/xml', './', copy);
        exists('../thirPart/leren/libs', './libs', copy);
        //拷贝icon        
        exists('../star/resources/gogostar', './assets/res/loading', copy);
    } else if (part == "tencent") {
        //拷贝files(来消星星的你)
        exists('../thirPart/tencent/src', './src', copy);
        exists('../thirPart/tencent/sdk', './sdk', copy);
        exists('../thirPart/tencent/res', './res', copy);
        exists('../thirPart/tencent/xml', './', copy);
        exists('../thirPart/tencent/libs', './libs', copy);
        exists('../thirPart/tencent/runtime', './runtime', copy);
    } else if (part == "cucc") {
        //拷贝files(来消星星的你)
        exists('../thirPart/cucc/src', './src', copy);
        exists('../thirPart/cucc/res', './res', copy);
        exists('../thirPart/cucc/xml', './', copy);
        exists('../thirPart/cucc/libs', './libs', copy);
        //拷贝icon        
        exists('../star/resources/popostar', './assets/res/loading', copy);
    } else if (part == "zhangle") {
        //拷贝files(星星去哪儿)
        exists('../thirPart/zhangle/src', './src', copy);
        exists('../thirPart/zhangle/sdk', './sdk', copy);
        exists('../thirPart/zhangle/res', './res', copy);
        exists('../thirPart/zhangle/xml', './', copy);
        exists('../thirPart/zhangle/libs', './libs', copy);
        //拷贝icon        
        exists('../star/resources/gogostar', './assets/res/loading', copy);
    } else if (part == "zhangle_yidongMM_sdk") {
        //拷贝files(星星去哪儿)        
        exists('../thirPart/zhangle_yidongMM_sdk/src', './src', copy);
        exists('../thirPart/zhangle_yidongMM_sdk/res', './res', copy);
        exists('../thirPart/zhangle_yidongMM_sdk/xml', './', copy);
        exists('../thirPart/zhangle_yidongMM_sdk/libs', './libs', copy);
        exists('../thirPart/zhangle_yidongMM_sdk/alipay_lib', './alipay_lib', copy);
        //拷贝icon        
        exists('../star/resources/gogostar', './assets/res/loading', copy);
        //拷贝superegg资源
        exists('../star/resources/zhangle_yidongMM_sdk/superegg', './assets/res/superegg', copy);
    } else if (part == "yingyongbao") {
        //拷贝files(来消星星的你)
        exists('../thirPart/yingyongbao/src', './src', copy);
        exists('../thirPart/yingyongbao/sdk', './sdk', copy);
        exists('../thirPart/yingyongbao/res', './res', copy);
        exists('../thirPart/yingyongbao/xml', './', copy);
        exists('../thirPart/yingyongbao/libs', './libs', copy);
        exists('../thirPart/yingyongbao/alipay_lib', './alipay_lib', copy);
        //拷贝icon
        exists('../star/resources/popostar', './assets/res/loading', copy);
    } else if (part == "360sdk") {
        //拷贝files(来消星星的你)
        exists('../thirPart/360sdk/src', './src', copy);
        exists('../thirPart/360sdk/res', './res', copy);
        exists('../thirPart/360sdk/xml', './', copy);
        exists('../thirPart/360sdk/libs', './libs', copy);
        //拷贝icon
        exists('../star/resources/popostar', './assets/res/loading', copy);
    }
    // console.log(argv[3]);
    // console.log(_version);
    // console.log(_part);
    setTimeout(function() {
        build_apk(version, lang, part);
    }, 100);
}

// 读取版本号（根据选择的渠道，读取对应渠道的版本号）
function apkVersion(part) {
    console.log(part);
    console.log(typeof(part));
    var vers;
    var path = "../thirPart/" + part + "/xml/AndroidManifest.xml";
    var rs = fs.readFile(path, function(err, data) {
        var xml = data.toString();
        // digester.digest方法将xml解析成了json格式的对象
        digester.digest(xml, function(err, result) {
            if (err) {
                console.log(err);
            } else { // 获取对象的属性
                vers = result['manifest']['android:versionName'];
            }
        });
        console.log(part);
        console.log("获取的版本号为" + vers);

        console.log(typeof(part));
        version = vers;
        // 开始调用main()函数
        main(part);
    });
}

// 开始创建apk
function build_apk(argv) {
    console.log(count);
    console.log(part);
    // 替换语言字符串
    replaceStringOfJava('chinese', lang);
    // 定义文件包名
    var apk_name = "./apk/star" + version + part + "_release" + tag + ".apk";
    console.log(apk_name);
    // 调用ant打包命令
    // ------------------------------注意SDK路径配置问题-------------------------------------
    clean = exec('call ant clean');
    clean.stdout.on('data', function(data) {
        console.log('clean标准输出：' + data);
    });
    clean.on('exit', function(code) {
        console.log('clean子进程已关闭，代码:' + code);

        release = exec('call ant release');
        release.stdout.on('data', function(data) {
            console.log('release标准输出：' + data);
        });
        release.on('exit', function(code) {
            console.log('release子进程已关闭，代码:' + code);
            // 拷贝apk
            copy_apk('./bin/star-release.apk', apk_name);
        });
        console.log(version);
        console.log(part);
    });
    process.on('exit', function() {
        console.log('打包完成，进程退出');
    });
}

// 拷贝apk文件
function copy_apk(src, dst) {
    rs = fs.createReadStream(src);
    ws = fs.createWriteStream(dst);
    rs.pipe(ws);
    //拷贝文件名输出
    console.log("copy||" + dst + "...");
    ws.on('data', function(data) {
        console.log('copy_apk' + data)
    });
    ws.on('exit', function(code) {
        //通过count判断选择的渠道
        all_count(part);
        console.log("copy_apk完成");
    });
    ws.emit('exit');
    console.log("count判断完成");
}

// 用于全部渠道打包时count计数
function all_count(part) {
    console.log(count);
    var step = count
    if (step < 7) {
        console.log('进来了');
        step++;
        count = step;
        var par = arr[count];
        part = par;
        console.log(part);
        //process.argv.splice(0, 4, '', '', '', part);
        return apkVersion(part);
    } else {
        console.log('没进去');
        return;
    }
}

// 获取实时时间
function packdate(apk_name) {
    var packdate = new Date();
    // 分别获取时间的对应数据
    year = packdate.getFullYear();
    month = packdate.getMonth() + 1; //js获取月份以0开始,'0'代表一月份。
    date = packdate.getDate();
    hours = packdate.getHours();
    minutes = packdate.getMinutes();
    seconds = packdate.getSeconds();
    //在小于10的数字面前加0
    if (month < 10) month = '0' + month;
    if (date < 10) date = '0' + date
    if (hours < 10) hours = '0' + hours;
    if (minutes < 10) minutes = '0' + minutes;
    if (seconds < 10) seconds = '0' + seconds;
    //拼接字符串为apk的文件名
    var apk_name = year + "" + month + "" + date + "" + hours + "" + minutes + "" + seconds;
    tag = apk_name;
}

// 根据对应的语言版本，拷贝对应的资源文件
function copy_res(src, dst, callback) {
    deleteDir('./assets/res');
    if (lang === 'english') {
        exists('../star/resources/res_en', './assets/res', copy);
    } else {
        exists('../star/resources/res_cn', './assets/res', copy);
    }
}

// 主程序运行
function main(part) {
    if (version !== null) {
        clearFiles();
        packdate();
        loadFiles(part);
        console.log(version);
        console.log(lang);
        console.log(part);
        console.log(tag);
    }
}


userInput();