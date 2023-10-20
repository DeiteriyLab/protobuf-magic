# Protobuf Magic 🪄

**Protobuf Magic** is an advanced extension for Burp Suite designed to intuitively handle Protobuf (Protocol Buffers) messages, even in the absence of complete .proto definitions. If you're familiar with tools like InQL, imagine that capability fine-tuned exclusively for Protobuf data.

## 🌟 Features

- **Protobuf Analysis**: Seamlessly interpret incoming Protobuf messages within Burp Suite's Proxy, Repeater, or Intruder, converting them into a format you can easily comprehend, all without needing the original .proto files.
- **Message Modification**: Freely adjust the Protobuf message contents. Test a variety of scenarios and edge cases without the hassle of meddling with .proto files.
- **Message Dispatch**: Dispatch altered Protobuf messages straight from Burp Suite towards your desired target—a vital toolset for assessing Protobuf-integrated APIs and software.
- **JSON to Protobuf Serialization**: Furnish a JSON payload within Intruder, and let the extension convert it to Protobuf right before dispatching the request.

## 🔧 Installation

1. Grab the freshest release right [here](https://github.com/DeiteriyLab/protobuf-magic/releases).
2. Journey to the "Extender" segment in Burp Suite. Hit "Add" inside the "Extensions" panel.
3. Opt for the JAR file you've just downloaded and proceed with "Next."
4. Voilà! Protobuf Magic integrates seamlessly into your Burp Suite, ready for action.

## 🚀 Usage

1. Post-installation, commence traffic interception that encompasses Protobuf messages via Proxy, Repeater, or Intruder.
2. Protobuf Magic springs into action, identifying and breaking down Protobuf messages within request and response data.
3. Within Proxy and Repeater, edit the Protobuf message content before relaying it server-wards.
4. "Send to Intruder" pushes your Protobuf message towards Intruder, primed for thorough testing or fuzzing.
5. **For JSON to Protobuf in Intruder**: Transfer your chosen JSON content into the body request portion of Intruder. The extension ensures JSON is molded into Protobuf before transmission.

<p align="center">
<img width="800" alt="send_image" src=".readme/send.png">
<img width="800" alt="psend_image" src=".readme/psend.png">
</p>

📹 **Visual Learners, Rejoice!** Dive into our [Video Guide](https://vimeo.com/876247400):
[![Vimeo Guide](https://i.vimeocdn.com/video/1741116953-29324df5a902222af8d39670b984002e8e4e1fcf0272f9aafaae05b84235301e-d.png)](https://vimeo.com/876247400)

## ⚠️ Limitations

- Despite its prowess, Protobuf Magic might occasionally stumble upon intricate Protobuf constructs if the core .proto definitions are out of reach. Consequently, certain nested or bespoke types might not render flawlessly.
- Tweaking Protobuf messages sans a precise message blueprint could culminate in distorted or erroneous server-bound dispatches.

## 📜 License

Protobuf Magic graces the open-source community under the umbrella of the GNU License. Dive deeper into our [LICENSE](https://github.com/DeiteriyLab/protobuf-magic/blob/main/LICENSE) for an in-depth look.
