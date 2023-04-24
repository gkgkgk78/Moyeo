import 'package:flutter/material.dart';

class OneButtonMaterialDialog {
  void showFeedBack(BuildContext context, String message) {
    final alert = AlertDialog(
      content: Text(
        message,
        style: const TextStyle(fontSize: 20),
      ),
      actions: [
        TextButton(
            child: const Text("OK"),
            onPressed: () {
              Navigator.of(context).pop();
            })
      ],
    );

    showDialog(
        context: context,
        builder: (BuildContext context) {
          return alert;
        });
  }
}
