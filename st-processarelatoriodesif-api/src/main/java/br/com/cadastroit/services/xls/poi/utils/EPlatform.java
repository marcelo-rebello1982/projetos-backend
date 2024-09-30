package br.com.cadastroit.services.xls.poi.utils;

public enum EPlatform {

	Any("any"), Linux("Linux"), Windows("Windows"), OS2("OS/2"), Solaris("Solaris"), SunOS("SunOS"), MPEiX("MPE/iX"),
	HP_UX("HP-UX"), AIX("AIX"), OS390("OS/390"), FreeBSD("FreeBSD"), Irix("Irix"), Digital_Unix("Digital Unix"),
	OpenVMS("OpenVMS"), Others("Others");

	private EPlatform(String desc) {
		this.description = desc;
	}

	public String toString() {
		return description;
	}

	private String description;
}