import 'package:flutter/material.dart';

class GradientText extends StatelessWidget {


  const GradientText(
      this.text, {
        required this.gradient,
        required this.style
      });

  final String text;
  final List<Color> gradient;
  final TextStyle style;

  @override
  Widget build(BuildContext context) {
    return ShaderMask(
      shaderCallback: (Rect bounds) {
        return LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: gradient,
        ).createShader(bounds);
      },
      blendMode: BlendMode.srcIn,
      child: Text(text, )
    );
  }
}