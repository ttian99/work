var fs = require('fs-extra');
// var EventEmitter = require('events').EventEmitter;
var pro = require('child_process').execFile;
var exec = require('child_process').exec;
var readline = require('readline');
var version, lang, part, tag;
var count = 1;
// 调用第三方包xml-digester来解析xml
var xml_digester = require("xml-digester");
var digester = xml_digester.XmlDigester({});
// var _logger = xml_digester._logger;
// _logger.level(_logger.TRACE_LEVEL);
// 调用prompt包来进行输入
var prompt = require('prompt');
var moment = require('moment');

var arr = ['no_platform', 'cmcc', 'leren', 'tencent', 'cucc', 'zhangle', 'zhangle_yidongMM_sdk', 'yingyongbao', '360sdk', 'ctcc', 'All'];
var arr_lang = ['chinese', 'english'];

function main() {
    userInput(function() {
        copyRes();

        // 开始版本号读取函数
        version = getApkVersion(function(vers) {
            version = vers;
            build();
        });
    });
}

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
            description: '请选择渠道: [0]空包 [1]移动MM  [2]乐人  [3]腾讯  [4]联通  [5]掌乐  [6]掌乐(移动妹妹)  [7]应用宝  [8]360sdk  [9]电信  [10]以上所有渠道.',
            pattern: /^[0-9]{1,2}$/,
            message: '请输入0-10之间的整数'
        }
    }
};

// 运行语言选择输入和渠道选择输入函数
function userInput(cb) {
    prompt.start();

    prompt.get(schema, function(err, result) {
        var _part = result.part;
        var _lang = result.lang;
        lang = arr_lang[_lang - 1];
        if (_part == 10) {
            console.log('打包全部渠道');
            part = arr[count];
            console.log(part);
        } else {
            part = arr[_part];
            count = _part + 100;
        }
        console.log('---------输入信息----------------------------');
        console.log('  你选择的语言为 ' + _lang + ' ' + lang);
        console.log('  您选择的渠道为 ' + _part + ' ' + part);
        console.log('  count数值为' + count);
        cb();
    });
}

// 根据对应的语言版本，拷贝对应的资源文件
function copyRes(src, dst, callback) {
    fs.removeSync('./assets/res');
    if (lang === 'english') {
        fs.copySync('../star/resources/res_en', './assets/res');
    } else {
        fs.copySync('../star/resources/res_cn', './assets/res');
    }
}

// 读取版本号（根据选择的渠道，读取对应渠道的版本号）
function getApkVersion(cb) {
    var vers;
    var path = "../thirPart/" + part + "/xml/AndroidManifest.xml";
    var rs = fs.readFile(path, function(err, data) {
        if (err) {
            console.log('read file err: ' + err);
        } else {
            var xml = data.toString();
            // digester.digest方法将xml解析成了json格式的对象
            digester.digest(xml, function(err, result) {
                if (err) {
                    console.log(err);
                } else { // 获取对象的属性
                    vers = result['manifest']['android:versionName'];
                    console.log("获取的版本号为" + vers);
                    cb(vers);
                }
            });
        }
    });
}

function copyExceptCocosLib(src, dst) {
    fs.copySync(src, dst, function(file) {
        if (src.indexOf('libcocos2djs.so') !== -1) {
            return false;
        }
        return true;
    });
}

// 主程序运行
function build() {
    if (version !== null) {
        clearFiles();
        tag = getBuildTime();
        loadFiles();
        buildApk();
    }
}

// 替换字符串操作
function replaceStringOfJava(fromId, toId) {
    var path = './src/com/methodsRun/methodsRun.java';
    var str = fs.readFileSync(path).toString();
    str = str.replace(fromId, toId);
    fs.writeFileSync(path, str, 'utf-8');
}

// 清除文件操作
function clearFiles() {
    fs.removeSync("./src");
    fs.removeSync("./sdk");
    fs.removeSync("./res");
    fs.removeSync("./libs");
    fs.removeSync("./alipay_lib");
    fs.removeSync("./runtime");
    fs.removeSync("./AndroidManifest.xml");
    fs.removeSync("./.classpath");
    fs.removeSync("./project.properties");
    fs.removeSync("./assets/res/loading/game_logo2.png");
    fs.removeSync("./assets/res/loading/game_logo.png");
}

// 拷贝文件操作
function loadFiles() {
    if (part == "no_platform") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/no_platform/src', './src');
        copyExceptCocosLib('../thirPart/no_platform/res', './res');
        copyExceptCocosLib('../thirPart/no_platform/xml', './');
        copyExceptCocosLib('../thirPart/no_platform/libs', './libs');
    } else if (part == "cmcc") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/cmcc/src', './src');
        //exists( '../thirPart/cmcc/sdk', './sdk');
        copyExceptCocosLib('../thirPart/cmcc/res', './res');
        copyExceptCocosLib('../thirPart/cmcc/xml', './');
        copyExceptCocosLib('../thirPart/cmcc/libs', './libs');
        copyExceptCocosLib('../thirPart/cmcc/alipay_lib', './alipay_lib');
        //拷贝icon
        copyExceptCocosLib('../star/resources/popostar', './assets/res/loading');
        //拷贝superegg资源
        copyExceptCocosLib('../star/resources/cmcc/superegg', './assets/res/superegg');
    } else if (part == "leren") {
        //拷贝files(星星去哪儿)
        copyExceptCocosLib('../thirPart/leren/src', './src');
        copyExceptCocosLib('../thirPart/leren/sdk', './sdk');
        copyExceptCocosLib('../thirPart/leren/res', './res');
        copyExceptCocosLib('../thirPart/leren/xml', './');
        copyExceptCocosLib('../thirPart/leren/libs', './libs');
        //拷贝icon        
        copyExceptCocosLib('../star/resources/gogostar', './assets/res/loading');
    } else if (part == "tencent") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/tencent/src', './src');
        copyExceptCocosLib('../thirPart/tencent/sdk', './sdk');
        copyExceptCocosLib('../thirPart/tencent/res', './res');
        copyExceptCocosLib('../thirPart/tencent/xml', './');
        copyExceptCocosLib('../thirPart/tencent/libs', './libs');
        copyExceptCocosLib('../thirPart/tencent/runtime', './runtime');
    } else if (part == "cucc") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/cucc/src', './src');
        copyExceptCocosLib('../thirPart/cucc/res', './res');
        copyExceptCocosLib('../thirPart/cucc/xml', './');
        copyExceptCocosLib('../thirPart/cucc/libs', './libs');
        //拷贝icon        
        copyExceptCocosLib('../star/resources/popostar', './assets/res/loading');
        //拷贝superegg资源
        copyExceptCocosLib('../star/resources/cucc/superegg', './assets/res/superegg');
    } else if (part == "zhangle") {
        //拷贝files(星星去哪儿)
        copyExceptCocosLib('../thirPart/zhangle/src', './src');
        copyExceptCocosLib('../thirPart/zhangle/res', './res');
        copyExceptCocosLib('../thirPart/zhangle/xml', './');
        copyExceptCocosLib('../thirPart/zhangle/libs', './libs');
        //拷贝icon        
        copyExceptCocosLib('../star/resources/gogostar', './assets/res/loading');
        //拷贝superegg资源
        copyExceptCocosLib('../star/resources/zhangle/superegg', './assets/res/superegg');
    } else if (part == "zhangle_yidongMM_sdk") {
        //拷贝files(星星去哪儿)        
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/src', './src');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/res', './res');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/xml', './');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/libs', './libs');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/alipay_lib', './alipay_lib');
        //拷贝icon        
        copyExceptCocosLib('../star/resources/gogostar', './assets/res/loading');
        //拷贝superegg资源
        copyExceptCocosLib('../star/resources/zhangle_yidongMM_sdk/superegg', './assets/res/superegg');
    } else if (part == "yingyongbao") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/yingyongbao/src', './src');
        copyExceptCocosLib('../thirPart/yingyongbao/sdk', './sdk');
        copyExceptCocosLib('../thirPart/yingyongbao/res', './res');
        copyExceptCocosLib('../thirPart/yingyongbao/xml', './');
        copyExceptCocosLib('../thirPart/yingyongbao/libs', './libs');
        copyExceptCocosLib('../thirPart/yingyongbao/alipay_lib', './alipay_lib');
        //拷贝icon
        copyExceptCocosLib('../star/resources/popostar', './assets/res/loading');
    } else if (part == "360sdk") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/360sdk/src', './src');
        copyExceptCocosLib('../thirPart/360sdk/res', './res');
        copyExceptCocosLib('../thirPart/360sdk/xml', './');
        copyExceptCocosLib('../thirPart/360sdk/libs', './libs');
        //拷贝icon
        copyExceptCocosLib('../star/resources/popostar', './assets/res/loading');
    } else if (part == "ctcc") {
        //拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/ctcc/src', './src');
        copyExceptCocosLib('../thirPart/ctcc/res', './res');
        copyExceptCocosLib('../thirPart/ctcc/xml', './');
        copyExceptCocosLib('../thirPart/ctcc/libs', './libs'); 
        copyExceptCocosLib('../thirPart/ctcc/assets', './assets');
        //拷贝icon
        copyExceptCocosLib('../star/resources/popostar', './assets/res/loading');
        //拷贝superegg资源
        copyExceptCocosLib('../star/resources/ctcc/superegg', './assets/res/superegg');
    }
}

// 开始创建apk
function buildApk() {
    console.log('buildApk count: ' + count);
    // 替换语言字符串
    replaceStringOfJava('chinese', lang);
    // 定义文件包名
    var apk_name = "./apk/star_" + version + "_" + part + "_release" + tag + ".apk";
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

            console.log('build succ dest name ' + apk_name);
            // 拷贝apk
            fs.copySync('./bin/star-release.apk', apk_name);
            if (needContinue()) {
                build();
            }

        });
    });
    process.on('exit', function() {
        console.log('打包完成，进程退出');
    });
}

// 用于全部渠道打包时count计数
function needContinue() {
    console.log("needContinue enter:" + count);
    var step = count
    if (step < 8) {
        console.log('进来了');
        step++;
        count = step;
        var par = arr[count];
        part = par;
        console.log('next channel is ' + part);
        return true;
    } else {
        console.log('没进去');
    }
    return false;
}

// 获取实时时间
function getBuildTime() {
    var now = moment();
    return now.format('YYYYMMDDHHmmss');
}

main();