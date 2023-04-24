class MyStack<T> {
  // 아니 Dart 스택 없는거 실화냐
  final _list = <T>[];

  void push(T value) => _list.add(value);

  void clear() => _list.clear();

  T? pop() {
    if (_list.isEmpty) return null;
    return _list.removeLast();
  }

  bool get isNotEmpty => _list.isNotEmpty;

  @override
  String toString() {
    return 'MyStack{_list: $_list}';
  }
}
