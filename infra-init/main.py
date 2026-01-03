import argparse
import yaml
import sys
import os
from scripts.kafka_init import run_kafka_init
from scripts.db_init import run_db_init
from scripts.kafka_gen import run_kafka_gen

def load_module_config(module_name):
    """
    Dynamically load the YAML config file for a specific module.
    Example: module_name='kafka' -> loads './config/kafka.yaml'
    """
    config_file = os.path.join(os.path.dirname(__file__), "config", f"{module_name}.yaml")

    if not os.path.exists(config_file):
        print(f"⚠️  Warning: Config file not found for module '{module_name}' at {config_file}")
        return {}

    with open(config_file, "r", encoding="utf-8") as f:
        return yaml.safe_load(f) or {}

def main():
    parser = argparse.ArgumentParser(description="Multi-Module Infrastructure Provisioner 2026")
    parser.add_argument("module", choices=["kafka", "kafka-gen", "db", "all"],
                        help="The infrastructure component to initialize")

    args = parser.parse_args()
    print(f"🚀 Initializing task: {args.module}")

    try:
        if args.module == "kafka":
            config = load_module_config("kafka")
            run_kafka_init(config)

        elif args.module == "db":
            config = load_module_config("database") # Match filename
            run_db_init(config)
        elif args.module == "kafka-gen":
            config = load_module_config("kafka")
            run_kafka_gen(config)
        elif args.module == "all":
            # Load and run each module independently
            kafka_cfg = load_module_config("kafka")
            run_kafka_init(kafka_cfg)

            db_cfg = load_module_config("database")
            run_db_init(db_cfg)

        print(f"\n✨ Done: {args.module} initialization finished.")

    except Exception as e:
        print(f"💥 Critical Failure: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()