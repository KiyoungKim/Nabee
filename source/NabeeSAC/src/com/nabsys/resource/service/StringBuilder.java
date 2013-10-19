package com.nabsys.resource.service;

import java.io.UnsupportedEncodingException;

public final class StringBuilder{
	java.lang.StringBuilder s = null;
	public StringBuilder() {
		s = new java.lang.StringBuilder(16);
	}
	public StringBuilder(int capacity) {
		s = new java.lang.StringBuilder(capacity);
	}
	public StringBuilder(String str) {
		s = new java.lang.StringBuilder(str);
	}
	public StringBuilder(CharSequence seq) {
		s = new java.lang.StringBuilder(seq.length() + 16);
		s.append(seq);
	}
	public StringBuilder append(Object obj) {
		return append(String.valueOf(obj));
	}
	public StringBuilder append(String str) {
		s.append(str);
		return this;
	}
	public StringBuilder append(String s, int start, int end) {
		this.s.append(s, start, end);
		return this;
	}
	public StringBuilder append(int i) {
		s.append(i);
		return this;
	}
	public StringBuilder append(long lng) {
		s.append(lng);
		return this;
	}
	public StringBuilder append(float f) {
		s.append(f);
		return this;
	}
	public StringBuilder append(double d) {
		s.append(d);
		return this;
	}
	public StringBuilder appendCodePoint(int codePoint) {
		s.appendCodePoint(codePoint);
		return this;
	}
	public StringBuilder delete(int start, int end) {
		s.delete(start, end);
		return this;
	}
	public StringBuilder deleteCharAt(int index) {
		s.deleteCharAt(index);
		return this;
	}
	public StringBuilder replace(int start, int end, String str) {
		s.replace(start, end, str);
		return this;
	}
	public StringBuilder insert(int index, char str[], int offset, int len) 
	{
		s.insert(index, str, offset, len);
		return this;
	}
	public StringBuilder insert(int offset, Object obj) {
		s.insert(offset, obj);
		return this;
	}
	public StringBuilder insert(int offset, String str) {
		s.insert(offset, str);
		return this;
	}
	public StringBuilder insert(int offset, char str[]) {
		s.insert(offset, str);
		return this;
	}
	public StringBuilder insert(int dstOffset, CharSequence s) {
		this.s.insert(dstOffset, s);
		return this;
	}
	public StringBuilder insert(int dstOffset, CharSequence s, int start, int end)
	{
		this.s.insert(dstOffset, s, start, end);
		return this;
	}
	public StringBuilder insert(int offset, boolean b) {
		s.insert(offset, b);
		return this;
	}
	public StringBuilder insert(int offset, char c) {
		s.insert(offset, c);
		return this;
	}
	public StringBuilder insert(int offset, int i) {
		s.insert(offset, i);
		return this;
	}
	public StringBuilder insert(int offset, long l) {
		s.insert(offset, l);
		return this;
	}
	public StringBuilder insert(int offset, float f) {
		s.insert(offset, f);
		return this;
	}
	public StringBuilder insert(int offset, double d) {
		s.insert(offset, d);
		return this;
	}
	public int indexOf(String str) {
		return s.indexOf(str);
	}
	public int indexOf(String str, int fromIndex) {
		return s.indexOf(str, fromIndex);
	}
	public int lastIndexOf(String str) {
		return s.lastIndexOf(str);
	}
	public int lastIndexOf(String str, int fromIndex) {
		return s.lastIndexOf(str, fromIndex);
	}
	public StringBuilder reverse() {
		s.reverse();
		return this;
	}
	public String toString() {
		return s.toString();
	}
	public int toIntger() throws NumberFormatException
	{
		return Integer.parseInt(s.toString());
	}
	public long toLong() throws NumberFormatException
	{
		return Long.parseLong(s.toString());
	}
	public float toFloat() throws NumberFormatException
	{
		return Float.parseFloat(s.toString());
	}
	public Double toDouble() throws NumberFormatException
	{
		return Double.parseDouble(s.toString());
	}
	public boolean equals(String target)
	{
		return s.toString().equals(target);
	}
	public int length() {
		return s.length();
	}
	public byte[] getBytes(String charsetName) throws UnsupportedEncodingException{
		return s.toString().getBytes(charsetName);
	}
	public byte[] getBytes() {
		return s.toString().getBytes();
	}
	public boolean equalsIgnoreCase(String anotherString) {
		return s.toString().equalsIgnoreCase(anotherString);
	}
	public int compareTo(String anotherString) {
		return s.toString().compareTo(anotherString);
	}
	public boolean regionMatches(int toffset, String other, int ooffset, int len) {
		return s.toString().regionMatches(toffset, other, ooffset, len);
	}
	public boolean regionMatches(boolean ignoreCase, int toffset,
			String other, int ooffset, int len) {
		return s.toString().regionMatches(ignoreCase, toffset, other, ooffset, len);
	}
	public boolean startsWith(String prefix, int toffset) {
		return s.toString().startsWith(prefix, toffset);
	}
	public boolean startsWith(String prefix) {
		return s.toString().startsWith(prefix);
	}
	public boolean endsWith(String suffix) {
		return s.toString().endsWith(suffix);
	}
	public StringBuilder substring(int beginIndex) {
		return new StringBuilder(s.toString().substring(beginIndex));
	}
	public StringBuilder substring(int beginIndex, int endIndex) {
		return new StringBuilder(s.toString().substring(beginIndex, endIndex));
	}
	public boolean matches(String regex) {
		return s.toString().matches(regex);
	}
	public boolean contains(String s) {
		return s.toString().contains(s);
	}
	public StringBuilder replaceFirst(String regex, String replacement) {
		return new StringBuilder(s.toString().replaceFirst(regex, replacement));
	}
	public StringBuilder replaceAll(String regex, String replacement) {
		return new StringBuilder(s.toString().replaceAll(regex, replacement));
	}
	public String[] split(String regex, int limit) {
		return s.toString().split(regex, limit);
	}
	public String[] split(String regex) {
		return s.toString().split(regex);
	}
	public StringBuilder toLowerCase() {
		return new StringBuilder(s.toString().toLowerCase());
	}
	public StringBuilder toUpperCase() {
		return new StringBuilder(s.toString().toUpperCase());
	}
	public StringBuilder trim() {
		return new StringBuilder(s.toString().trim());
	}
}