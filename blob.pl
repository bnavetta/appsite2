#!/usr/bin/env perl

use Number::Bytes::Human;

my $parser = Number::Bytes::Human->new;

if(scalar(@ARGV) != 2)
{
	die "Usage: $0 <outfile> <blocksize>"
}

my $outfile = $ARGV[0];
my $amount = $parser->parse($ARGV[1]);

my @cmd = ('dd', "of=$outfile", "if=/dev/random", "bs=1024", "count=" . $amount/1024);

system(@cmd) == 0 or die "dd failed: $?"