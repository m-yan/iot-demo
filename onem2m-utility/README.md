oneM2M Utility
===

oneM2MのIN-CSEとMca binding MQTTで通信するためのJavaユーテリティパッケージです。  
大きくわけて以下の2つの機能があります。  

1. IN-CSEとMQTTで通信するための機能
2. 1で送受信するoneM2M準拠のJSON形式のペイロードをデシリアライズしてオブジェクトに変換する機能、および、その逆

## Requirement

* 本パッケージでIN-CSEと通信するためには、事前にIN-CSEに対してAE Registrationを行い、IN-AE-IDを払い出してもらう必要があります。
* 本パッケージを利用する上で、oneM2Mの仕様の理解がある程度必要になります。   
必要に応じてoneM2M release2 TS-0001,0004,0010を参照してください。


## Usage

### MQTTでの通信

#### MQTT Connect

    MqttConnection conn = MqttConnections.getConnectionTo("tcp://mqttbroker:1883"); 
    conn.connect();

#### MQTT Connect + Subscribe（受信）

    MqttConnection conn = MqttConnections.getConnectionTo("tcp://mqttbroker:1883");
    conn.connect();
    String topic = TopicReference.getTopicForRequestFromCseTo("IN-AE-ID");
    conn.subscribe(topic, receiver);  // receiverは、MqttMessageProcessableインターフェースを実装したクラスのインスタンス。受信したメッセージの処理はreceiverが担う。


#### MQTT Publish（送信）

    MqttConnection conn = MqttConnections.getConnectionTo("tcp://mqttbroker:1883");
    String topic = TopicReference.getTopicForResponseToCseFrom("IN-AE-ID");
    conn.publish(topic, bytePayload)


#### MQTT Close

    conn.close();

#### 留意点

* oneM2Mでは、MQTTの接続はIN-AE毎に1つの接続とする、かつ、それを常時接続してその上でPub/Subする、ように規定されています。
* そのため、本パッケージは、MQTT Connect/Subscribe/Closeはプログラムの起動終了時に行うものとし、  
通常の通信時（publish)は、MqttConnectionsから都度、接続済のMqttConnectionインスタンスを取得して利用するデザインになっています。  
(=Publishの度にConnect/Closeはしない）  
* IN-AEごとに1接続の制限のため、ConnectionPoolではなく1インスタンスをマルチスレッドで使いまわす方式です。   
スレッドセーフになっている（はず）ので問題はないと思いますが、  
想定された使い方の範疇（プログラム起動停止時にシングルスレッドでconnect+subscribe/closeをする、平時はマルチスレッドでpublishする）での利用を推奨します。
* その他、APIの詳細は、org.onem2m.mca.mqtt.client.MqttConnectionのJavaDocを参照してください。

---

### oneM2Mペイロードとオブジェクトの相互変換

#### 例1. 以下のようなNotify Requestをデシリアライズしてpc(primitiveContent)に指定されたcin(contentInstance)のcon(content)を取り出す場合

    {
        "op": 5,
        "to": "C001227",
        "fr": "/PN_CSE",
        "rqi": "20170517300000",
        "pc": {
            "cin": {
                "ty": 4,
                "ri": "ri001239",
                "rn": "ri001239",
                "ct": "20170517T142841",
                "lt": "20170517T142841",
                "pi": "ri001229",
                "cs": "40",
                "con": "eyJzZW5zb3IiOnsidmFsdWUiOiIzNTMuMzIifX19"
            }
        }
    }

Javaコード

    RequestPrimitive request = RequestPrimitive.valueOf(strJsonSerializedRequest);
    ContentInstance cin = request.getContentCastedBy(ContentInstance.class);
    cin.getContent();

#### 例2. 以下のようなResponseをデシリアライズしてrsc(responseStatusCode)を取り出す場合

    {
        "rqi": "20170517000001",
        "rsc": 2001
    }

Javaコード

    ReponsePrimitive response = ResponsePrimitive.valueOf(strJsonSerialozedResponse);
    response.getResponseStatusCode();



#### 例3. contentInstanceをcontainer下にCreateするリクエストを生成する場合

    ContentInstance cin = new ContentInstance("XXX");
    RequestPrimitive request = RequestPrimitive.newCreateRequest("container-URI", "IN-AE-ID", cin);
    reqeust.toJson();

 以下が生成される。

    {
        "op": 1,
        "to": "container-URI",
        "fr": "IN-AE-ID",
        "rqi": "yyyymmddhhmiss+randomな5文字の文字列",
        "ty": 4,
        "pc": {
            "cin": {
                "con": "XXX"
            }
        }
    }

#### 例4. 受信したリクエストに対して2000（OK）を応答するレスポンスを生成する場合

    RequestPrimitive request = RequestPrimitive.valueOf(strJsonSerializedRequest);
    ResponsePrimitive response = new ResponsePrimitive(ResponseStatus.OK, request);
    response.toJson();

 以下が生成される。
 
    {
        "rqi": "受信したリクエストのrqi",
        "rsc": 2000
    }

## Install

### 利用するプロジェクトでMavenを使う場合

ローカルレポジトリに登録

    $ mvnw install

利用先のpom.xmlのdependenciesに以下を追加 

    <dependency>
        <groupId>org.onem2m</groupId>
        <artifactId>onem2m-utility</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

---

### 利用するプロジェクトでMavenを使わない場合

パッケージ作成

    $ mvnw package

target下のjarファイルを利用先のプロジェクトのクラスパスに追加
