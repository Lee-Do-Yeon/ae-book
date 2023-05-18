import pytorch_lightning as pl
import torch
import torch.nn as nn
from transformers import AutoTokenizer,AutoModelForSequenceClassification,BertForSequenceClassification

device = "cuda" if torch.cuda.is_available() else "cpu"

class TextClassificationStudentModule(pl.LightningModule):
    def __init__(self, config, labels, lr=5e-4, alpha=1.0):
        super().__init__()
        self.save_hyperparameters()
        if isinstance(config, str):
            self.model = AutoModelForSequenceClassification.from_pretrained(
                config, num_labels=len(labels)
            )
        else:
            self.model = BertForSequenceClassification(config)
        self.multiclass = len(labels) > 1
        self.criterion = nn.CrossEntropyLoss() if self.multiclass else nn.BCELoss()
        self.soft_label_criterion = nn.BCELoss()  # nn.KLDivLoss(reduction='batchmean')
        self.labels = labels

    def configure_optimizers(self):
        opt = optim.Adam(self.parameters(), lr=self.hparams.lr)
        return opt
        # sched = optim.lr_scheduler.StepLR(opt, 200, 0.5)
        # return [opt], [sched]

    def forward(self, input_ids, attention_mask=None):
        logits = self.model(input_ids, attention_mask=attention_mask).logits
        if self.multiclass:
            logits = logits.softmax(dim=-1)
        else:
            logits = logits.sigmoid().squeeze(1).float()
        return logits

    def _shared_step(self, batch):
        ids, masks, labels, soft_labels = batch
        alpha = self.hparams.alpha

        logits = self(ids, masks)
        ce_loss = self.criterion(logits, labels)
        kd_loss = self.soft_label_criterion(logits, soft_labels)
        loss = alpha * ce_loss + (1 - alpha) * kd_loss

        return {
            "loss": loss,
            "logits": logits,
            "labels": labels,
            "ce_loss": ce_loss,
            "kd_loss": kd_loss,
        }

    def training_step(self, batch, batch_idx):
        return self._shared_step(batch)

    def validation_step(self, batch, batch_idx):
        return self._shared_step(batch)

    def _shared_epoch_end(self, outputs, stage):
        outputs = join_step_outputs(outputs)
        loss_names = ["loss", "ce_loss", "kd_loss"]
        for name in loss_names:
            loss = outputs[name].mean()
            self.log(f"{stage}_epoch_{name}", loss, prog_bar=True)

        logits = outputs["logits"]
        labels = outputs["labels"]
        acc = tm.accuracy(logits, labels.int())
        self.log(f"{stage}_acc", acc, prog_bar=True)

    def training_epoch_end(self, outputs):
        self._shared_epoch_end(outputs, "train")

    def validation_epoch_end(self, outputs):
        self._shared_epoch_end(outputs, "val")

tokenizer_curse = AutoTokenizer.from_pretrained("monologg/koelectra-small-v3-discriminator")


hate = "./curse/hate.ckpt"
curse = "./curse/curse.ckpt"

curse = TextClassificationStudentModule.load_from_checkpoint(curse, device)
hate = TextClassificationStudentModule.load_from_checkpoint(hate, device)

def detect_curse(text):
    
    with torch.no_grad():
        model_input = tokenizer_curse(text, return_tensors="pt")
        curse_pred = curse(
            model_input["input_ids"], model_input["attention_mask"]
        )[0].item()
        hate_pred = hate(model_input["input_ids"], model_input["attention_mask"])[
            0
        ].item()
    
    if curse_pred >= 0.7 or hate_pred >= 0.6 or (curse_pred+hate_pred) >= 0.85:
        
        return 1
    
    else:
        
        return 0
    