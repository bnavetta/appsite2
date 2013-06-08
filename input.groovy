File out = new File(args[0])
out.withOutputStream { os ->
	1024.times {
		os.write((it as String).getBytes("UTF-8"))
		os.write("\n".getBytes("UTF-8"))
	}
}
