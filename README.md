TextView自定义省略号. 仿微博
思路:根据行数获取合适text在添加自定义省略号
参考:EllipsizedMultilineTextView和AppCompatTextHelper, EllipsizeTextView(https://github.com/dinuscxj/EllipsizeTextView)


自定义Ellipsize
```java
EllipsizeTextView ellipsizeTextView = findViewById(R.id.contentView);
EditText editText = findViewById(R.id.inputView);

SpannableString sp = new SpannableString("...全文");
sp.setSpan(new ForegroundColorSpan(Color.RED), 0, sp.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
ellipsizeTextView.setText(editText.getText(), sp);
```