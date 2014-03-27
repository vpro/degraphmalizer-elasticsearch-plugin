degraphmalizer-elasticsearch-plugin
===================================

Elasticsearch plugin that sends HTTP requests to a Degraphmalizer instance.
Configure degraphmalizer-elasticsearch-plugin

For installing instruction see https://github.com/vpro/degraphmalizer


## The ES plugin

The Elasticsearch plugin provides the Degraphmalizer with notifications of the changes in Elasticsearch.
It consists of the following components:

### Listeners

3 Types:
- One index lifecycle listener, which will watch for changes in the indexes and their shards and will register/deregister an indexshard listener for each indexshard.
- An indexshard listener for every indexshard to watch for document changes in the index, it sends these to the manager
- One cluster listener to look for the presence of a running Degrahphmalizer, and tell the manager about it.

### Updater

For each index there will be an updater which will receive the document changes from the manager and forward them
to the degraphmalizer. It maintains an internal queue for this for when the Degraphmalizer is not available. This queue will overflow
to disk if it gets to large.

### Manager

There is a manager which manages the updaters, and passes changes to the right updater. It will also pause the updaters when
there is no Degraphmalizer active (it gets called by the cluster listener for this).

### JMX Bean

For monitoring the queue sizes.

# The future
- Push configuration to `/_degraphmalize/`
- Replicate the graph to some other machines
- Watch every "index" request
- Perform degraphmalizing on one machine


