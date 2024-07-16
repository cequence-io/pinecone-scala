import json
from datasets import load_dataset

def exec():
    trec = load_dataset('trec', split='train')

    with open("trec-train.json", "a") as trecFile:
        for i in range(0, len(trec)):
            trecFile.write(json.dumps(trec[i]) + "\n")

    print("Finished")

if __name__ == "__main__":
    exec()
