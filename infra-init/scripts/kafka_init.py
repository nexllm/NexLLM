import json
import os

import requests
from confluent_kafka.admin import AdminClient, NewTopic

def init_kafka_topics(admin_client, topics_config):
    new_topics = []
    for t in topics_config:
        new_topics.append(NewTopic(
            topic=t['name'],
            num_partitions=t['partitions'],
            replication_factor=t['replication_factor'],
            config=t.get('configs', {})
        ))

    fs = admin_client.create_topics(new_topics)
    for topic, f in fs.items():
        try:
            f.result()  # 等待操作完成
            print(f"✅ Topic '{topic}' created success")
        except Exception as e:
            print(f"⚠️ Topic '{topic}' status: {e}")

def register_schema(registry_url, topic_name, schema_path, compatibility):
    subject = f"{topic_name}-value"
    schema_path = os.path.join(os.path.dirname(__file__), schema_path)

    with open(schema_path, "r") as f:
        schema_json = json.load(f)

    reg_url = f"{registry_url}/subjects/{subject}/versions"
    res = requests.post(reg_url, json={"schema": json.dumps(schema_json)},
                        headers={"Content-Type": "application/json"})

    if res.status_code == 200:
        print(f"✅ Schema created: {subject} (ID: {res.json()['id']})")
    else:
        print(f"❌ Schema create failed: {subject} - {res.text}")
        return

    conf_url = f"{registry_url}/config/{subject}"
    requests.put(conf_url, json={"compatibility": compatibility})
    print(f"⚙️  Compatibility: {compatibility}")

def run_kafka_init(kafka_config):

    """Initializes Kafka based on the passed config dictionary."""
    if not kafka_config:
        print("  ❌ Kafka config is empty. Skipping.")
        return

    # Existing logic to create topics and register schemas...
    print(f"  ✅ Connecting to: {kafka_config.get('bootstrap_servers')}")
    admin_client = AdminClient({"bootstrap.servers": kafka_config['bootstrap_servers']})
    print("init kafka topics ")
    init_kafka_topics(admin_client, kafka_config['topics'])

    for t_conf in kafka_config['topics']:
        if 'schema_file' in t_conf:
            register_schema(
                kafka_config['schema_registry_url'],
                t_conf['name'],
                t_conf['schema_file'],
                t_conf.get('compatibility', 'BACKWARD')
            )
