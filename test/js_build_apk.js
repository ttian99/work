var fs = require('fs-extra');
// var EventEmitter = require('events').EventEmitter;
var pro = require('child_process').execFile;
var exec = require('child_process').exec;
var readline = require('readline');
var version, lang, part, tag, block;
var count = 1;
// 调用第三方包xml-digester来解析xml
var xml_digester = require("xml-digester");
var digester = xml_digester.XmlDigester({});
// var _logger = xml_digester._logger;
// _logger.level(_logger.TRACE_LEVEL);
// 调用prompt包来进行输入
var prompt = require('prompt');
var moment = require('moment');

var arr_cn = ['no-platform', 'cmcc', 'leren', 'tencent', 'cucc', 'zhangle', 'zhangle_yidongMM_sdk', 'yingyongbao', '360sdk', 'ctcc', 'xiaomi', 'All'];
var arr_lang = ['chinese', 'english', 'vietnamese'];
var arr_en = ['no-platform'];
var arr_vn = ['no-platform', 'ALAO'];
var arr_block = ['block1', 'block2'];

function main() {
    choiceLanguage(function(putlang) {
        // 开始选择渠道号
        console.log('main putlang:' + putlang);
        choicePart(putlang, function() {
            // 开始版本号读取函数
            console.log(part);
            version = getApkVersion(function(vers) {
                version = vers;
                build();
            });
        });
        //build();
    });
}

// 创建prompt用的对象
var inputLang = {
    properties: {
        lang: {
            description: '请选语言版本:[1]中文(chinese) [2]英文(english) [3]越南(Vietnamese)\n\n',
            //default: '1',
            pattern: /^[1-3]+$/,
            message: '输入参数不正确',
            required: true
        },
        block: {
            description: '请选择方块类型:[1] block1 [2] block2',
            default: '1',
            pattern: /^[1-2]$/,
            message: ' 输入参数不正确',
            required: true
        }
    }
};
var en_par = {
    properties: {
        part: {
            description: '请选择渠道: [0]空包 \n',
            //default: '0',
            pattern: /^[0]$/,
            message: '请输入0-10之间的整数',
            required: true
        }
    }
};
var vn_par = {
    properties: {
        part: {

            description: '请选择渠道: [0]空包 [1]ALAO\n',
            //default: '0',
            pattern: /^[0-1]$/,
            message: '请输入0-1之间的整数',
            required: true
        }
    }
};
var cn_par = {
    properties: {
        part: {
            description: '请选择渠道: [0]空包 [1]移动MM  [2]乐人  [3]腾讯  [4]联通  [5]掌乐  [6]掌乐(移动妹妹)  [7]应用宝  [8]360sdk  [9]电信  [10]小米  [11]以上所有渠道.\n\n',
            //default: '0',
            pattern: /^[0-9]{1,2}$/,
            message: '请输入0-10之间的整数',
            required: true
        }
    }
};

// 选择语言版本
function choiceLanguage(callback) {
    prompt.start();
    prompt.get(inputLang, function(err, result) {
        var putlang, putblock;
        putlang = result.lang;
        lang = arr_lang[putlang - 1];
        putblock = result.block;
        block = arr_block[putblock - 1];
        console.log('语言版本为' + putlang + lang);
        callback(putlang, block);
    });

}

// 选择渠道号
function choicePart(putlang, callback) {
    var inputpart;
    if (putlang == 1) {
        prompt.get(cn_par, function(err, result) {
            inputpart = result.part;
            //判断是否选择全部渠道打包
            if (inputpart == 11) {
                console.log('您选择打包全部渠道');
                part = arr_cn[count];
                console.log(part);
            } else {
                part = arr_cn[inputpart];
                count = inputpart + 100;
            }
            console.log('中文版本的渠道为:' + result.part + part);
            callback(inputpart);
        });
    } else if (putlang == 2) {
        prompt.get(en_par, function(err, result) {
            inputpart = result.part;
            part = arr_en[inputpart];
            count = inputpart + 100;
            console.log('英文版本的渠道为:' + result.part);
            callback(inputpart);
        });
    } else {
        prompt.get(vn_par, function(err, result) {
            inputpart = result.part;
            part = arr_vn[inputpart];
            count = inputpart + 100;
            console.log('越南版本的渠道为:' + result.part);
            callback(inputpart);
        });
    }
}

// 根据对应的语言版本，拷贝对应的资源文件
function copyRes(src, dst, callback) {
    fs.removeSync('./assets/res');
    if (lang === 'english') {
        fs.copySync('../star/resources/res_en', './assets/res');
    } else if (lang === 'vietnam') {
        fs.copySync('../star/resources/res_vn', './assets/res');
    } else if (lang === 'chinese') {
        fs.copySync('../star/resources/res_cn', './assets/res');
    }
}

// 选择使用哪种方块
function copyBlock() {
    fs.copySync('../star/resources/' + block, './assets/res/core');
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
        clearFiles(function() {
            //开始拷贝对应的语言版本资源
            copyRes();
            copyBlock();
        });
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
function clearFiles(callback) {
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
    callback();
}

// 拷贝文件操作
function loadFiles() {
    if (part == "no-platform") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/no-platform/src', './src');
        copyExceptCocosLib('../thirPart/no-platform/res', './res');
        copyExceptCocosLib('../thirPart/no-platform/xml', './');
        copyExceptCocosLib('../thirPart/no-platform/libs', './libs');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/popostar/loading', './assets/res/loading');
    } else if (part == "cmcc") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/cmcc/src', './src');
        copyExceptCocosLib('../thirPart/cmcc/res', './res');
        copyExceptCocosLib('../thirPart/cmcc/xml', './');
        copyExceptCocosLib('../thirPart/cmcc/libs', './libs');
        copyExceptCocosLib('../thirPart/cmcc/alipay_lib', './alipay_lib');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/popostar/loading', './assets/res/loading');
        // 拷贝superegg资源
        copyExceptCocosLib('../star/resources/cmcc/superegg', './assets/res/superegg');
    } else if (part == "leren") {
        // 拷贝files(星星去哪儿)
        copyExceptCocosLib('../thirPart/leren/src', './src');
        copyExceptCocosLib('../thirPart/leren/sdk', './sdk');
        copyExceptCocosLib('../thirPart/leren/res', './res');
        copyExceptCocosLib('../thirPart/leren/xml', './');
        copyExceptCocosLib('../thirPart/leren/libs', './libs');
        //拷贝icon        
        copyExceptCocosLib('../star/resources/gogostar1/loading', './assets/res/loading');
    } else if (part == "tencent") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/tencent/src', './src');
        copyExceptCocosLib('../thirPart/tencent/sdk', './sdk');
        copyExceptCocosLib('../thirPart/tencent/res', './res');
        copyExceptCocosLib('../thirPart/tencent/xml', './');
        copyExceptCocosLib('../thirPart/tencent/libs', './libs');
        copyExceptCocosLib('../thirPart/tencent/runtime', './runtime');
    } else if (part == "cucc") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/cucc/src', './src');
        copyExceptCocosLib('../thirPart/cucc/res', './res');
        copyExceptCocosLib('../thirPart/cucc/xml', './');
        copyExceptCocosLib('../thirPart/cucc/libs', './libs');
        // 拷贝icon        
        copyExceptCocosLib('../star/resources/popostar/loading', './assets/res/loading');
        // 拷贝superegg资源
        copyExceptCocosLib('../star/resources/cucc/superegg', './assets/res/superegg');
    } else if (part == "zhangle") {
        // 拷贝files(星星去哪儿)
        copyExceptCocosLib('../thirPart/zhangle/src', './src');
        copyExceptCocosLib('../thirPart/zhangle/res', './res');
        copyExceptCocosLib('../thirPart/zhangle/xml', './');
        copyExceptCocosLib('../thirPart/zhangle/libs', './libs');
        // 拷贝icon        
        copyExceptCocosLib('../star/resources/gogostar1/loading', './assets/res/loading');
        // 拷贝superegg资源
        copyExceptCocosLib('../star/resources/zhangle/superegg', './assets/res/superegg');
    } else if (part == "zhangle_yidongMM_sdk") {
        // 拷贝files(星星去哪儿)        
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/src', './src');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/res', './res');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/xml', './');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/libs', './libs');
        copyExceptCocosLib('../thirPart/zhangle_yidongMM_sdk/alipay_lib', './alipay_lib');
        // 拷贝icon        
        copyExceptCocosLib('../star/resources/gogostar1/loading', './assets/res/loading');
        // 拷贝superegg资源
        copyExceptCocosLib('../star/resources/zhangle_yidongMM_sdk/superegg', './assets/res/superegg');
    } else if (part == "yingyongbao") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/yingyongbao/src', './src');
        copyExceptCocosLib('../thirPart/yingyongbao/sdk', './sdk');
        copyExceptCocosLib('../thirPart/yingyongbao/res', './res');
        copyExceptCocosLib('../thirPart/yingyongbao/xml', './');
        copyExceptCocosLib('../thirPart/yingyongbao/libs', './libs');
        copyExceptCocosLib('../thirPart/yingyongbao/alipay_lib', './alipay_lib');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/popostar/loading', './assets/res/loading');
    } else if (part == "360sdk") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/360sdk/src', './src');
        copyExceptCocosLib('../thirPart/360sdk/res', './res');
        copyExceptCocosLib('../thirPart/360sdk/xml', './');
        copyExceptCocosLib('../thirPart/360sdk/libs', './libs');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/gogostar2/loading', './assets/res/loading');
        //拷贝superegg资源
        copyExceptCocosLib('../star/resources/360pay/superegg', './assets/res/superegg');
    } else if (part == "ctcc") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/ctcc/src', './src');
        copyExceptCocosLib('../thirPart/ctcc/res', './res');
        copyExceptCocosLib('../thirPart/ctcc/xml', './');
        copyExceptCocosLib('../thirPart/ctcc/libs', './libs');
        copyExceptCocosLib('../thirPart/ctcc/assets', './assets');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/popostar/loading', './assets/res/loading');
        // 拷贝superegg资源(超值神奇蛋)
        copyExceptCocosLib('../star/resources/ctcc/superegg', './assets/res/superegg');
    } else if (part == 'ALAO') {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/ALAO/src', './src');
        copyExceptCocosLib('../thirPart/ALAO/res', './res');
        copyExceptCocosLib('../thirPart/ALAO/xml', './');
        copyExceptCocosLib('../thirPart/ALAO/libs', './libs');
        copyExceptCocosLib('../thirPart/ALAO/assets', './assets');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/res_vn/loading', './assets/res/loading');
    } else if (part == "xiaomi") {
        // 拷贝files(来消星星的你)
        copyExceptCocosLib('../thirPart/xiaomi/src', './src');
        copyExceptCocosLib('../thirPart/xiaomi/res', './res');
        copyExceptCocosLib('../thirPart/xiaomi/xml', './');
        copyExceptCocosLib('../thirPart/xiaomi/libs', './libs');
        copyExceptCocosLib('../thirPart/xiaomi/assets', './assets');
        // 拷贝icon
        copyExceptCocosLib('../star/resources/popostar/loading', './assets/res/loading');
        // 拷贝superegg资源
        copyExceptCocosLib('../star/resources/xiaomi/superegg', './assets/res/superegg');
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
        // console.log('clean标准输出：' + data);
    });
    clean.on('exit', function(code) {
        console.log('clean子进程已关闭，代码:' + code);

        release = exec('call ant release');
        release.stdout.on('data', function(data) {
            // console.log('release标准输出：' + data);
        });
        release.on('exit', function(code) {
            console.log('release子进程已关闭，代码:' + code);
            //console.log('build succ dest name ' + apk_name);
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
    if (step < 9) {
        console.log('继续打包计数' + count);
        step++;
        count = step;
        var par = arr_cn[count];
        part = par;
        console.log('next channel is ' + part);
        return true;
    } else {
        console.log('不需要继续打包');
    }
    return false;
}

// 获取实时时间
function getBuildTime() {
    var now = moment();
    return now.format('YYYYMMDDHHmmss');
}

main();