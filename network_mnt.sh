#!/usr/bin/sh

# Default values
DIR="${HOME}/network_mnt"
SERVER="192.168.0.100"
EXPORT="data_storage/"
ACTION="mount" # Default action is to mount

# Function to display usage
usage() {
    echo "Usage: $0 [-d directory] [-s server] [-e export] [-a action]"
    echo "  -d directory : Directory to mount/unmount (absolute path)"
    echo "  -s server    : NFS server IP or hostname"
    echo "  -e export    : Export path on the NFS server"
    echo "  -a action    : Action to perform ('mount' or 'unmount')"
    exit 1
}

# Parse command-line options
while getopts ":d:s:e:a:" opt; do
    case ${opt} in
        d ) DIR=$OPTARG ;;
        s ) SERVER=$OPTARG ;;
        e ) EXPORT=$OPTARG ;;
        a ) ACTION=$OPTARG ;;
        \? ) usage ;;
        : ) echo "Invalid option: -$OPTARG requires an argument" >&2; usage ;;
    esac
done
shift $((OPTIND -1))

# Validate directory path
if [[ ! "$DIR" =~ ^/ ]]; then
    echo "Error: Directory path must be absolute." >&2
    exit 1
fi

# Validate action
if [[ "$ACTION" != "mount" && "$ACTION" != "unmount" ]]; then
    echo "Error: Invalid action '$ACTION'. Use 'mount' or 'unmount'." >&2
    usage
fi

# Function to execute a command with pkexec
run_with_pkexec() {
    local cmd="$*"
    echo "Running command with pkexec: $cmd"
    if ! pkexec bash -c "$cmd"; then
        echo "Error: Failed to execute command with pkexec." >&2
        exit 1
    fi
}

# Function to mount the NFS share
mount_nfs() {
    if [ ! -d "$DIR" ]; then
        echo "Directory does not exist. Creating now..."
        mkdir -p "$DIR" || { echo "Failed to create directory: $DIR" >&2; exit 1; }
        echo "Directory created."
    else
        echo "Directory already exists."
    fi

    # Validate server reachability
    if ! ping -c 1 -W 2 "$SERVER" &> /dev/null; then
        echo "Error: NFS server $SERVER is not reachable." >&2
        exit 1
    fi

    # Check if directory is already mounted
    if mountpoint -q "$DIR"; then
        echo "$DIR is already mounted."
    else
        echo "$DIR is not mounted. Attempting to mount..."
        run_with_pkexec "mount -t nfs4 '$SERVER:$EXPORT' '$DIR'"
        echo "Mount successful."
    fi
}

# Function to unmount the NFS share
unmount_nfs() {
    if mountpoint -q "$DIR"; then
        echo "$DIR is mounted. Attempting to unmount..."
        run_with_pkexec "umount '$DIR'"
        echo "Unmount successful."
    else
        echo "$DIR is not mounted."
    fi
}

# Perform the requested action
case "$ACTION" in
    mount) mount_nfs ;;
    unmount) unmount_nfs ;;
esac
