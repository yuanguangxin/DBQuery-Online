// 提示
let tip = function (msg) {
    layer.closeAll();
    layer.open({
        type: 1,
        id: 'noNon',
        content: '<div style="padding: 20px 100px;">' + msg + '</div>',
        btn: '关闭',
        btnAlign: 'c',
        shade: 0,
        yes: function () {
            layer.closeAll();
        }
    });
};

// 删除所有子节点，用来替换innerHTML
let deleteChildren = function (rootDom) {
    let child = rootDom.lastElementChild;
    while (child) {
        rootDom.removeChild(child);
        child = rootDom.lastElementChild;
    }
};

// 判断是否是线上库
let isOnline = function (envVal) {
    return envVal === env.db1;
};

// 切换DB测试链接可用性
let switchCustomDb = function (url, user, pass) {
    layer.load();
    axios.get('/sql/switchCustomDb', {
        params: {
            env: localStorage.env,
            url: url,
            user: user,
            pass: pass
        }
    }).then(function (res) {
        layer.closeAll();
        if (res.data.message !== 'ok') {
            tip(res.data.message);
            return;
        }
        if (res.data.obj[0].ok === false) {
            tip('DB连接失败，请检查DB参数');
            return;
        }
        layer.msg('已连接：' + url);
    }).catch(function () {
        layer.closeAll();
        tip('系统异常，请稍后再试');
    });
};

// 点击自定义数据源事件
let clickCustomDb = function () {
    let url = localStorage.url ? localStorage.url : '';
    let user = localStorage.user ? localStorage.user : '';
    let pass = localStorage.pass ? localStorage.pass : '';
    if (url === '' || user === '' || pass === '') {
        layer.prompt({
            formType: 0,
            value: url,
            title: 'ip+端口（示例：10.75.0.1:3306）',
        }, function (value, index) {
            url = value;
            localStorage.url = value;
            layer.close(index);
            layer.prompt({
                formType: 0,
                value: user,
                title: '用户名',
            }, function (value, index) {
                user = value;
                localStorage.user = value;
                layer.close(index);
                layer.prompt({
                    formType: 1,
                    value: pass,
                    title: '密码',
                }, function (value, index) {
                    pass = value;
                    localStorage.pass = value;
                    layer.close(index);
                    switchCustomDb(url, user, pass);
                });
            });
        });
        return;
    }
    switchCustomDb(url, user, pass);
};

// 切换数据源事件
window.onload = function () {
    let envTabs = document.getElementsByClassName('envTab');
    for (let envTab of envTabs) {
        envTab.onclick = function () {
            let envVal = this.getAttribute('envVal');
            if (envVal) {
                localStorage.env = envVal;
                let newValue = localStorage['saveSql' + localStorage.env];
                editor.setValue(newValue ? newValue : '', 1);

                // 如果是db2（有多分片）
                if (envVal === env.db2) {
                    document.getElementById('tableNo').style.display = 'inline';
                    document.getElementById('executeAll').style.display = 'inline';
                    document.getElementById('downloadAll').style.display = 'inline';
                } else {
                    document.getElementById('tableNo').style.display = 'none';
                    document.getElementById('executeAll').style.display = 'none';
                    document.getElementById('downloadAll').style.display = 'none';
                }

                // 如果是线上库
                if (isOnline(envVal)) {
                    document.getElementById('insertToOffline').style.display = 'inline';
                } else {
                    document.getElementById('insertToOffline').style.display = 'none';
                }

                // xstp和自定义数据源特殊逻辑
                if (envVal === env.customDb) {
                    document.getElementById('resetConn').style.display = 'inline';
                    clickCustomDb();
                } else {
                    document.getElementById('resetConn').style.display = 'none';
                }
            } else {
                document.getElementById('env' + localStorage.env).classList.add('layui-this');
                this.classList.remove('layui-this');
            }
        };
    }
};

// 构造异常返回结果视图table
let createErrorTable = function (msg) {
    let table = document.getElementById('resultTable');
    deleteChildren(table);
    let thead = document.createElement('thead');
    let thead_tr = document.createElement('tr');
    let thead_th = document.createElement('th');
    thead_th.innerText = 'Error';
    table.appendChild(thead);
    thead.appendChild(thead_tr);
    thead_tr.appendChild(thead_th);

    let tbody = document.createElement('tbody');
    let tbody_tr = document.createElement('tr');
    let tbody_td = document.createElement('td');
    tbody_td.innerText = msg;
    tbody_td.style.color = '#FF5722';
    table.appendChild(tbody);
    tbody.appendChild(tbody_tr);
    tbody_tr.appendChild(tbody_td);
};

// 构造单个返回结果视图table
let createSingletonTable = function (obj) {
    let table = document.getElementById('resultTable');
    deleteChildren(table);
    let tbody = document.createElement('tbody');
    table.appendChild(tbody);
    let data = obj[0];
    for (let key in data) {
        if (!data.hasOwnProperty(key)) {
            continue;
        }
        let tbody_tr = document.createElement('tr');
        let tbody_td_key = document.createElement('td');
        let tbody_td_value = document.createElement('td');
        tbody_tr.appendChild(tbody_td_key);
        tbody_tr.appendChild(tbody_td_value);
        tbody_td_key.innerText = key;
        tbody_td_value.innerText = data[key] == null ? 'NULL' : data[key];
        tbody.appendChild(tbody_tr);
    }
};

// 构造通用返回结果视图table
let createObjTable = function (obj) {
    let head = [];
    for (let key in obj[0]) {
        if (!obj[0].hasOwnProperty(key)) {
            continue;
        }
        head.push(key);
    }
    let table = document.getElementById('resultTable');
    deleteChildren(table);
    let thead = document.createElement('thead');
    let thead_tr = document.createElement('tr');
    table.appendChild(thead);
    thead.appendChild(thead_tr);
    for (let thText of head) {
        let thead_th = document.createElement('th');
        thead_th.innerText = thText;
        thead_tr.appendChild(thead_th);
    }

    let tbody = document.createElement('tbody');
    table.appendChild(tbody);
    for (let data of obj) {
        let tbody_tr = document.createElement('tr');
        for (let key in data) {
            if (!data.hasOwnProperty(key)) {
                continue;
            }
            let tbody_td = document.createElement('td');
            tbody_td.innerText = data[key] == null ? 'NULL' : data[key];
            tbody_tr.appendChild(tbody_td);
        }
        tbody.appendChild(tbody_tr);
    }
};

// 双击转化为insert语句
let convert2Insert = function (text, tr) {
    let splitText = text.toUpperCase().split(' ');
    let fromIndex = splitText.findIndex(item => {
        return item === 'FROM';
    });
    let tableName = text.split(' ')[fromIndex + 1];

    let sql = 'insert into ' + tableName.replace('\n', '').replace(';', '') + '(';
    let thead = document.getElementsByTagName('thead')[0];
    let ths = thead.childNodes[0].childNodes;
    for (let i = 0; i < ths.length; i++) {
        sql += '`' + ths[i].innerText + '`';
        if (i !== ths.length - 1) {
            sql += ',';
        }
    }
    sql += ') values (';
    let tds = tr.childNodes;
    for (let i = 0; i < tds.length; i++) {
        if (tds[i].innerText === 'NULL') {
            sql += tds[i].innerText;
        } else if (isNaN(tds[i].innerText) || tds[i].innerText === '') {
            sql += "'" + tds[i].innerText + "'";
        } else {
            sql += tds[i].innerText;
        }
        if (i !== tds.length - 1) {
            sql += ',';
        }
    }
    sql += ');';
    return sql;
};

// 执行SQL
let doSql = function (allPartition, insertToOffline) {
    let text = editor.getSelectedText();
    if (text === '') {
        text = editor.session.getTextRange({
            end: editor.selection.getCursor(),
            start: {
                row: editor.selection.getCursor().row,
                column: 0
            }
        });
        let supportPrefix = ['SELECT', 'SHOW', 'DESC', 'EXPLAIN', 'USE', 'INSERT', 'DELETE',
            'UPDATE', 'CREATE', 'ALTER', 'TRUNCATE', 'DROP', 'FLUSH'];
        let support = false;
        for (let prefix of supportPrefix) {
            if (text.toUpperCase().startsWith(prefix)) {
                support = true;
            }
        }
        if (text === '' || !support) {
            text = editor.getValue();
        }
    }
    if (text === '') {
        return;
    }
    text = text.trim();

    let hasG = false;
    if (text.endsWith('\\G')) {
        text = text.substring(0, text.length - 2);
        hasG = true;
    }
    let userId = document.getElementById('userId').value;
    if (isNaN(userId) || userId === '') {
        userId = -1;
    }
    let url = '';
    let user = '';
    let pass = '';
    if (localStorage.env === env.customDb) {
        url = localStorage.url ? localStorage.url : '';
        user = localStorage.user ? localStorage.user : '';
        pass = localStorage.pass ? localStorage.pass : '';
    }
    layer.load();
    axios.post('/sql/doSql',
        Qs.stringify({
            sql: text,
            userId: userId,
            env: localStorage.env,
            url: url,
            user: user,
            pass: pass,
            allPartition: allPartition,
            insertToOffline: insertToOffline
        })
    ).then(function (res) {
        layer.closeAll();
        if (res.data.message !== 'ok') {
            tip(res.data.message);
            return;
        }
        let obj = res.data.obj;
        if (!obj || obj.length === 0 || obj[0] === null) {
            obj = [{'message': 'Empty set'}];
        }
        if (obj[0].ok === false) {
            createErrorTable(obj[0].message);
            return;
        }
        if (text.toUpperCase().startsWith('SELECT') && obj[0].ok !== false
            && obj[0].message !== 'Empty set' && obj.length === 1 && hasG) {
            createSingletonTable(obj);
        } else {
            createObjTable(obj);
        }
        let tds = document.getElementsByTagName('td');
        for (let td of tds) {
            td.onclick = function () {
                let temp = document.createElement('textarea');
                temp.value = td.innerText;
                document.body.appendChild(temp);
                temp.select();
                document.execCommand('Copy');
                temp.style.display = 'none';
                layer.msg('已复制');
            };
        }
        if (text.toUpperCase().startsWith('SELECT') && obj[0].ok !== false && !hasG) {
            if (obj[0].message) {
                if (obj[0].message === 'Empty set' || obj[0].message.indexOf('OK, Affected rows') !== -1) {
                    return;
                }
            }
            layer.msg('单击可复制文本, 双击可复制为INSERT语句');
            let trs = document.getElementsByTagName('tr');
            for (let tr of trs) {
                tr.ondblclick = function () {
                    let temp = document.createElement('textarea');
                    temp.value = convert2Insert(text, this);
                    document.body.appendChild(temp);
                    temp.select();
                    document.execCommand('Copy');
                    temp.style.display = 'none';
                    layer.msg('已复制为INSERT语句');
                };
            }
        }
    }).catch(function () {
        layer.closeAll();
        tip('系统异常，请稍后再试');
    });
};

// 全分片执行事件
document.getElementById('executeAll').onclick = function () {
    doSql(true, false);
};

// 线上数据导入线下点击事件
document.getElementById('insertToOffline').onclick = function () {
    let text = editor.getSelectedText();
    if (text === '') {
        tip('请选中Sql');
        return;
    }
    doSql(false, true);
};

// 重置连接点击事件
document.getElementById('resetConn').onclick = function () {
    if (localStorage.env === env.customDb) {
        localStorage.removeItem('url');
        localStorage.removeItem('user');
        localStorage.removeItem('pass');
        clickCustomDb();
    }
};

// SQL格式化
document.getElementById('formatSql').onclick = function () {
    layer.load();
    beautify.beautify(editor.session);
    layer.closeAll();
};

// 清空编辑区
document.getElementById('clearAll').onclick = function () {
    layer.confirm('确认清空吗', function (index) {
        editor.setValue('');
        layer.close(index);
    });
};

// 下载事件
let downLoadEvent = function (allPartition) {
    let text = editor.getSelectedText();
    if (text === '') {
        tip('请选中Sql');
        return;
    }
    let userId = document.getElementById('userId').value;
    if (isNaN(userId) || userId === '') {
        userId = -1;
    }
    location.href = '/sql/download?env=' + localStorage.env + '&sql=' + encodeURI(text)
        + '&userId=' + userId + '&allPartition=' + allPartition;
};

// 全分片下载
document.getElementById('downloadAll').onclick = function () {
    downLoadEvent(true);
};

// 下载
document.getElementById('download').onclick = function () {
    downLoadEvent(false);
};

// 获取分表数
document.getElementById('userId').oninput = function () {
    let userId = this.value;
    if (isNaN(userId) || userId === '') {
        tip('请输入正确的userid');
        return;
    }
    document.getElementById('tbno').innerText = '分表数：' + parseInt(userId % 8, 10);
};

// 获取分表数兼容有些人喜欢按Enter键
document.getElementById('userId').onkeydown = function (event) {
    if (event.keyCode === 13) {
        this.blur();
        event.cancleBubble = true;
        event.returnValue = false;
        return false;
    }
};

// 执行SQL事件
document.getElementById('execute').onclick = function () {
    doSql(false, false);
};